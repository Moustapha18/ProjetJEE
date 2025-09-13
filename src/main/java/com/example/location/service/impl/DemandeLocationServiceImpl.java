// src/main/java/com/example/location/service/impl/DemandeLocationServiceImpl.java
package com.example.location.service.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.DemandeLocationDao;
import com.example.location.dao.impl.DemandeLocationDaoJpa;
import com.example.location.entity.*;
import com.example.location.service.DemandeLocationService;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DemandeLocationServiceImpl implements DemandeLocationService {

    private final DemandeLocationDao dao;

    public DemandeLocationServiceImpl() { this(new DemandeLocationDaoJpa()); }
    public DemandeLocationServiceImpl(DemandeLocationDao dao) { this.dao = dao; }

    @Override
    public DemandeLocation create(Long appartementId, Long locataireUserId, String message) {
        dao.findPendingByAppAndUser(appartementId, locataireUserId).ifPresent(d -> {
            throw new IllegalStateException("Vous avez déjà une demande en attente pour cet appartement.");
        });

        EntityManager em = JpaUtil.getEntityManager();
        try {
            Appartement aRef = em.getReference(Appartement.class, appartementId);
            Utilisateur uRef = em.getReference(Utilisateur.class, locataireUserId);

            DemandeLocation d = new DemandeLocation();
            d.setAppartement(aRef);
            d.setLocataire(uRef);
            d.setMessage(message);
            d.setStatut(DemandeStatut.EN_ATTENTE);
            d.setCreatedAt(LocalDateTime.now());
            return dao.save(d);
        } finally {
            em.close();
        }
    }

    @Override
    public DemandeLocation updateStatut(Long demandeId, DemandeStatut statut) {
        DemandeLocation d = dao.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable"));

        if (statut == DemandeStatut.ACCEPTEE) {
            createContratFrom(d);
        }
        d.setStatut(statut);
        return dao.update(d);
    }

    @Override
    public Optional<DemandeLocation> findById(Long id) { return dao.findById(id); }

    @Override
    public List<DemandeLocation> findAll() { return dao.findAll(); }

    @Override
    public List<DemandeLocation> findByLocataire(Long userId) { return dao.findByLocataireId(userId); }

    @Override
    public List<DemandeLocation> listForProprietaire(Long proprietaireId) {
        return dao.findByProprietaireId(proprietaireId);
    }

    @Override
    public List<DemandeLocation> listAll() {
        return null;
    }

    // --------------------------------------------------------
    private void createContratFrom(DemandeLocation d) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long appId = d.getAppartement().getId();

            Long existing = em.createQuery("""
                select count(c) from Contrat c
                where c.appartement.id = :aid and c.dateFin is null
            """, Long.class)
                    .setParameter("aid", appId)
                    .getSingleResult();
            if (existing != null && existing > 0) {
                throw new IllegalStateException("Cet appartement est déjà sous contrat.");
            }

            Utilisateur user = d.getLocataire();
            if (user == null) throw new IllegalStateException("Utilisateur (locataire) introuvable sur la demande.");

            JpaUtil.begin(em);

            // Trouver ou créer le Locataire (table locataires) à partir de l'email du compte utilisateur
            Locataire loc = em.createQuery("""
                select l from Locataire l where lower(l.email) = lower(:email)
            """, Locataire.class)
                    .setParameter("email", user.getEmail())
                    .setMaxResults(1)
                    .getResultStream().findFirst().orElse(null);

            if (loc == null) {
                loc = new Locataire();
                loc.setEmail(user.getEmail());
                loc.setNomComplet(user.getUsername() != null ? user.getUsername() : "Locataire");
                em.persist(loc);
                em.flush();
            }

            Appartement aRef = em.getReference(Appartement.class, appId);

            Contrat c = new Contrat();
            c.setAppartement(aRef);
            c.setLocataire(loc);
            c.setLoyerMensuel(aRef.getLoyer());
            c.setCaution(aRef.getLoyer());        // par défaut = 1 mois
            c.setDateDebut(LocalDate.now());
            c.setDateFin(null);
            c.setStatut(resolveContratActif());
            try { c.setCreatedAt(LocalDateTime.now()); } catch (Exception ignored) {}

            em.persist(c);
            JpaUtil.commit(em);

            // ✅ Génère 12 échéances de paiements (mois) pour ce contrat
            new com.example.location.service.impl.PaiementServiceImpl()
                    .ensureScheduleForContract(c.getId(), c.getDateDebut(), c.getDateDebut().plusMonths(12));

        } catch (RuntimeException ex) {
            JpaUtil.rollback(em); throw ex;
        } finally {
            em.close();
        }
    }

    private StatutContrat resolveContratActif() {
        String[] candidates = {"EN_COURS", "ENCOURS", "ACTIF", "ACTIVE", "EN_COUR"};
        for (String name : candidates) {
            for (StatutContrat sc : StatutContrat.values()) {
                if (sc.name().equalsIgnoreCase(name)) return sc;
                String norm = sc.name().replace("_", "");
                if (norm.equalsIgnoreCase(name.replace("_", ""))) return sc;
            }
        }
        return StatutContrat.values()[0];
    }
}
