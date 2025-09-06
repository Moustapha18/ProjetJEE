package com.example.location.service.impl;

import com.example.location.dao.UtilisateurDao;
import com.example.location.dao.impl.UtilisateurDaoJpa;
import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Utilisateur;
import com.example.location.security.PasswordHasher;
import com.example.location.security.Role;
import com.example.location.service.UtilisateurService;

import java.util.Optional;

public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurDao dao;

    public UtilisateurServiceImpl() { this(new UtilisateurDaoJpa()); }
    public UtilisateurServiceImpl(UtilisateurDao dao) { this.dao = dao; }

    @Override
    public Optional<UtilisateurDto> authenticate(String username, String rawPassword) {
        return dao.findByUsername(username)
                .filter(u -> PasswordHasher.matches(rawPassword, u.getPasswordHash()))
                .map(u -> new UtilisateurDto(u.getId(), u.getUsername(), u.getEmail(), u.getRole()));
    }

    @Override
    public UtilisateurDto register(String username, String email, String rawPassword, Role role) {
        Utilisateur u = new Utilisateur();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(PasswordHasher.hash(rawPassword));
        u.setRole(role == null ? Role.USER : role);
        Utilisateur saved = dao.save(u);
        return new UtilisateurDto(saved.getId(), saved.getUsername(), saved.getEmail(), saved.getRole());
    }

    @Override
    public Optional<Utilisateur> findEntityByUsername(String username) {
        return dao.findByUsername(username);
    }
}
