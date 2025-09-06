package com.example.location.service;

import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Utilisateur;
import com.example.location.security.Role;

import java.util.Optional;

public interface UtilisateurService {
    Optional<UtilisateurDto> authenticate(String username, String rawPassword);
    UtilisateurDto register(String username, String email, String rawPassword, Role role);
    Optional<Utilisateur> findEntityByUsername(String username);
}
