package com.example.location.dao;

import com.example.location.entity.Utilisateur;
import java.util.Optional;

public interface UtilisateurDao extends CrudDao<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findByEmail(String email);
}
