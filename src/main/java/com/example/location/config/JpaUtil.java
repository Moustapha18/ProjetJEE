package com.example.location.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtil {

    private static final String PU_NAME = "PU";
    private static final EntityManagerFactory EMF = buildEmf();

    private JpaUtil() {}

    private static EntityManagerFactory buildEmf() {
        try {
            return Persistence.createEntityManagerFactory(PU_NAME);
        } catch (Exception e) {
            System.err.println("[JpaUtil] Échec init EMF: " + e.getMessage());
            throw e;
        }
    }

    /** Ouvre un EM — alias compatible avec ton code existant */
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /** Ouvre un EM — nom court, utilisé par certains DAO */
    public static EntityManager em() {
        return EMF.createEntityManager();
    }

    /** Démarre une transaction si elle n’est pas active */
    public static void begin(EntityManager em) {
        if (em != null && em.getTransaction() != null && !em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    /** Commit sûr */
    public static void commit(EntityManager em) {
        if (em != null && em.getTransaction() != null && em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    /** Rollback sûr (sans casser l’exception initiale) */
    public static void rollback(EntityManager em) {
        try {
            if (em != null && em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception ignore) {}
    }

    /** Ferme l’EMF si besoin (optionnel sous Tomcat) */
    public static void close() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
