package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.PaiementDao;
import com.example.location.entity.Paiement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class PaiementDaoJpa implements PaiementDao {

    @Override
    public List<Paiement> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                    "select p from Paiement p " +
                            "join fetch p.contrat c " +
                            "join fetch c.appartement a " +
                            "join fetch a.immeuble i " +
                            "join fetch c.locataire l " +
                            "order by p.id desc", Paiement.class
            ).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Paiement> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Paiement p = em.find(Paiement.class, id);
            if (p != null) {
                // optionnel: toucher les associations
                p.getContrat().getId();
                p.getContrat().getLocataire().getId();
                p.getContrat().getAppartement().getId();
            }
            return Optional.ofNullable(p);
        } finally {
            em.close();
        }
    }

    @Override
    public Paiement save(Paiement entity) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (entity.getId() == null) {
                em.persist(entity);      // CREATE
            } else {
                entity = em.merge(entity); // UPDATE
            }
            tx.commit();
            return entity;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // ⚠️ SUPPRIMER toute méthode update(...) avec @Override : elle n’existe pas dans l’interface
    //    et provoque l’erreur "does not override or implement..."
    //    -> on n’en a pas besoin puisque save() gère create/update.

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Paiement ref = em.find(Paiement.class, id);
            if (ref != null) em.remove(ref);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
