package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.ContratDao;
import com.example.location.entity.Contrat;
import com.example.location.entity.StatutContrat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class ContratDaoJpa implements ContratDao {

    @Override
    public Contrat save(Contrat c) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(c);
            tx.commit();
            return c;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }

    @Override
    public Contrat update(Contrat c) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Contrat merged = em.merge(c);
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Contrat found = em.find(Contrat.class, id);
            if (found != null) em.remove(found);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally { em.close(); }
    }

    @Override
    public Optional<Contrat> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Contrat.class, id));
        } finally { em.close(); }
    }

    // ContratDaoJpa.java
    @Override
    public List<Contrat> findAll() {
        var em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select c " +
                                    " from Contrat c " +
                                    " join fetch c.appartement a " +
                                    " join fetch a.immeuble " +
                                    " join fetch c.locataire " +
                                    " order by c.id desc", Contrat.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    @Override
    public List<Contrat> findByStatut(StatutContrat statut) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("select c from Contrat c where c.statut = :s order by c.id desc", Contrat.class)
                    .setParameter("s", statut)
                    .getResultList();
        } finally { em.close(); }
    }
}
