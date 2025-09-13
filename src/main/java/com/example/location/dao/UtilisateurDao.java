package com.example.location.dao;

import com.example.location.entity.Utilisateur;
import java.util.Optional;

public interface UtilisateurDao extends CrudDao<Utilisateur, Long> {

    Optional<Utilisateur> findByResetToken(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findByEmail(String email);
}
