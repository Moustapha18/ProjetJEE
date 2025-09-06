package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.AppartementDao;
import com.example.location.entity.Appartement;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class AppartementDaoJpa implements AppartementDao {

    @Override
    public Appartement save(Appartement a) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            em.persist(a);
            JpaUtil.commit(em);
            return a;
        } catch (Exception e) {
            JpaUtil.rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Appartement update(Appartement a) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Appartement merged = em.merge(a);
            JpaUtil.commit(em);
            return merged;
        } catch (Exception e) {
            JpaUtil.rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Appartement ref = em.find(Appartement.class, id);
            if (ref != null) em.remove(ref);
            JpaUtil.commit(em);
        } catch (Exception e) {
            JpaUtil.rollback(em);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Appartement> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Appartement.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<Appartement> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select a from Appartement a
                join fetch a.immeuble i
                order by a.id desc
            """, Appartement.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Appartement> findByImmeubleId(Long immeubleId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select a from Appartement a
                join fetch a.immeuble i
                where i.id = :id
                order by a.id desc
            """, Appartement.class)
                    .setParameter("id", immeubleId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
