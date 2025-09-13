package com.example.location.service;

import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Utilisateur;
import com.example.location.security.Role;

import java.util.List;
import java.util.Optional;

public interface UtilisateurService {
    Optional<UtilisateurDto> authenticate(String username, String rawPassword);
    UtilisateurDto register(String username, String email, String password, Role role);

    void requestPasswordReset(String email);                 // génère un token + expiration
    boolean resetPassword(String token, String newPassword); // applique le nouveau mot de passe

    Optional<Utilisateur> findEntityByUsername(String username);

    List<Utilisateur> adminFindAll();
    Optional<Utilisateur> adminFindById(Long id);
    Utilisateur adminCreate(String username, String email, String rawPassword, Role role);
    Utilisateur adminUpdateRole(Long id, Role role);
    void adminResetPassword(Long id, String newPassword);
    void adminDelete(Long id);

}
