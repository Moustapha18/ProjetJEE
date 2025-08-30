package com.example.location.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * SRP: classe dédiée à la création/gestion d'EntityManagerFactory.
 * DIP: les services/DAO dépendent de l'interface JPA (EntityManager), pas d'une impl concrète.
 */
public final class JpaUtil {
    private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("PU");
    private JpaUtil() {}
    public static EntityManager em() { return EMF.createEntityManager(); }
    public static void close() { EMF.close(); }
}
