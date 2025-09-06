package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.LocataireDao;
import com.example.location.entity.Locataire;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class LocataireDaoJpa implements LocataireDao {

    @Override
    public List<Locataire> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("select l from Locataire l order by l.id desc", Locataire.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Locataire> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Locataire.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Locataire save(Locataire l) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(l);
            em.getTransaction().commit();
            return l;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Locataire update(Long id, Locataire data) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Locataire l = em.find(Locataire.class, id);
            if (l == null) throw new IllegalArgumentException("Locataire introuvable: " + id);
            l.setNomComplet(data.getNomComplet());
            l.setEmail(data.getEmail());
            l.setTelephone(data.getTelephone());
            em.getTransaction().commit();
            return l;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Locataire l = em.find(Locataire.class, id);
            if (l != null) em.remove(l);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
