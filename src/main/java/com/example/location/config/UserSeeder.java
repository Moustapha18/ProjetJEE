package com.example.location.config;

import com.example.location.entity.Utilisateur;
import com.example.location.security.PasswordHasher;
import com.example.location.security.Role;
import jakarta.persistence.EntityManager;

public class UserSeeder {

    public static void seedUser(EntityManager em, String username, String email, String plainPassword, Role role) {
        Utilisateur user = new Utilisateur();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordHasher.hash(plainPassword));
        user.setRole(role);
        em.persist(user);
        System.out.printf("[Seeder] %s seedé : %s / %s%n", role, username, plainPassword);
    }
}
