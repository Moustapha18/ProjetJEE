package com.example.location.service.impl;

import com.example.location.dao.UtilisateurDao;
import com.example.location.dao.impl.UtilisateurDaoJpa;
import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Utilisateur;
import com.example.location.security.PasswordHasher;
import com.example.location.security.Role;
import com.example.location.service.UtilisateurService;

import java.util.List;
import java.util.Optional;

public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurDao dao;

    public UtilisateurServiceImpl() {
        this(new UtilisateurDaoJpa());
    }

    public UtilisateurServiceImpl(UtilisateurDao dao) {
        this.dao = dao;
    }

    @Override
    public Optional<UtilisateurDto> authenticate(String username, String rawPassword) {
        return dao.findByUsername(username)

                .filter(u -> PasswordHasher.matches(rawPassword, u.getPasswordHash()))
                .map(u -> new UtilisateurDto(u.getId(), u.getUsername(), u.getEmail(), u.getRole()));

    }

    @Override
    public UtilisateurDto register(String username, String email, String password, Role role) {
        if (role == null) role = Role.LOCATAIRE;
        // validations simples
        if (dao.existsByUsername(username)) throw new IllegalArgumentException("Nom d'utilisateur déjà pris");
        if (dao.existsByEmail(email)) throw new IllegalArgumentException("Email déjà utilisé");

        Utilisateur u = new Utilisateur();
        u.setUsername(username.trim());
        u.setEmail(email.trim());
        u.setPasswordHash(PasswordHasher.hash(password));
        u.setRole(role);
        Utilisateur saved = dao.save(u);
        return new UtilisateurDto(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole());
    }

    @Override
    public void requestPasswordReset(String email) {
        Optional<Utilisateur> uOpt = dao.findByEmail(email);
        if (uOpt.isEmpty()) return; // ne pas révéler l'existence de l'email

        Utilisateur u = uOpt.get();
        String token = java.util.UUID.randomUUID().toString().replace("-", ""); // 32 chars
        u.setResetToken(token);
        u.setResetExpires(java.time.LocalDateTime.now().plusMinutes(30)); // 30 min
        dao.save(u);

        // Ici tu enverras un email réel. Pour dev : on affiche en log
        System.out.println("[RESET] Lien de réinitialisation: /reset?token=" + token);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        Optional<Utilisateur> uOpt = dao.findByResetToken(token);
        if (uOpt.isEmpty()) return false;

        Utilisateur u = uOpt.get();
        if (u.getResetExpires() == null || u.getResetExpires().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }
        u.setPasswordHash(PasswordHasher.hash(newPassword));
        u.setResetToken(null);
        u.setResetExpires(null);
        dao.save(u);
        return true;
    }

    @Override public List<Utilisateur> adminFindAll() {
        return dao.findAll();
    }

    @Override public Optional<Utilisateur> adminFindById(Long id) {
        return dao.findById(id);
    }

    @Override public Utilisateur adminCreate(String username, String email, String rawPassword, Role role) {
        if (dao.existsByUsername(username)) throw new IllegalArgumentException("Nom d'utilisateur déjà pris");
        if (dao.existsByEmail(email)) throw new IllegalArgumentException("Email déjà utilisé");
        if (role == null) role = Role.LOCATAIRE;

        Utilisateur u = new Utilisateur();
        u.setUsername(username.trim());
        u.setEmail(email.trim());
        u.setPasswordHash(PasswordHasher.hash(rawPassword));
        u.setRole(role);
        return dao.save(u);
    }

    @Override public Utilisateur adminUpdateRole(Long id, Role role) {
        Utilisateur u = dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        u.setRole(role);
        return dao.save(u);
    }

    @Override public void adminResetPassword(Long id, String newPassword) {
        Utilisateur u = dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        u.setPasswordHash(PasswordHasher.hash(newPassword));
        dao.save(u);
    }

    @Override
    public void adminDelete(Long id) {
        // Vérifier les références avant suppression
        var em = com.example.location.config.JpaUtil.getEntityManager();
        try {
            Long nbImmeubles = em.createQuery(
                            "select count(i) from Immeuble i where i.proprietaire.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();

            if (nbImmeubles != null && nbImmeubles > 0) {
                throw new IllegalStateException("Suppression impossible : cet utilisateur est propriétaire de " + nbImmeubles + " immeuble(s). Réassignez ou supprimez ces immeubles d’abord.");
            }

            // TODO: faire pareil pour d’autres entités liées (Contrat, Demande, Paiement) si besoin

            dao.deleteById(id);
        } finally {
            em.close();
        }
    }



    @Override
    public Optional<Utilisateur> findEntityByUsername(String username) {
        return dao.findByUsername(username);
    }
}
