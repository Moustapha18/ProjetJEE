package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.ImmeubleDao;
import com.example.location.entity.Immeuble;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static com.example.location.config.JpaUtil.*;

public class ImmeubleDaoJpa implements ImmeubleDao {

    @Override public Optional<Immeuble> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();


        try { return Optional.ofNullable(em.find(Immeuble.class, id)); }
        finally { em.close(); }
    }

    @Override public List<Immeuble> findAll() {
        EntityManager em = JpaUtil.getEntityManager();

        try { return em.createQuery("select i from Immeuble i", Immeuble.class).getResultList(); }
        finally { em.close(); }
    }

    @Override public Immeuble save(Immeuble i) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            if (i.getId() == null) { em.persist(i); }
            else { i = em.merge(i); }
            em.getTransaction().commit();
            return i;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    @Override public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            Immeuble ref = em.find(Immeuble.class, id);
            if (ref != null) em.remove(ref);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }
}
