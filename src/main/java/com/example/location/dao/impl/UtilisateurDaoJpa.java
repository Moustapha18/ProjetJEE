package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.UtilisateurDao;
import com.example.location.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import com.example.location.security.Role;

public class UtilisateurDaoJpa implements UtilisateurDao {

    @Override public Optional<Utilisateur> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();

        try { return Optional.ofNullable(em.find(Utilisateur.class, id)); }
        finally { em.close(); }
    }

    @Override
    public Optional<Utilisateur> findByResetToken(String token) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Utilisateur> list = em.createQuery(
                            "select u from Utilisateur u where u.resetToken = :t", Utilisateur.class)
                    .setParameter("t", token)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty()? Optional.empty() : Optional.of(list.get(0));
        } finally { em.close(); }
    }

    @Override
    public boolean existsByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long c = em.createQuery(
                            "select count(u) from Utilisateur u where lower(u.username)=lower(:x)", Long.class)
                    .setParameter("x", username)
                    .getSingleResult();
            return c > 0;
        } finally { em.close(); }
    }

    @Override
    public boolean existsByEmail(String email) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long c = em.createQuery(
                            "select count(u) from Utilisateur u where lower(u.email)=lower(:x)", Long.class)
                    .setParameter("x", email)
                    .getSingleResult();
            return c > 0;
        } finally { em.close(); }
    }

    @Override public List<Utilisateur> findAll() {
        EntityManager em = JpaUtil.getEntityManager();

        try { return em.createQuery("select u from Utilisateur u", Utilisateur.class).getResultList(); }
        finally { em.close(); }
    }

    @Override public Utilisateur save(Utilisateur u) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            if (u.getId() == null) {
                em.persist(u);
            } else {
                u = em.merge(u);
            }
            em.getTransaction().commit();
            return u;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    @Override public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            Utilisateur ref = em.find(Utilisateur.class, id);
            if (ref != null) em.remove(ref);
            em.getTransaction().commit();
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally { em.close(); }
    }

    @Override public Optional<Utilisateur> findByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            List<Utilisateur> list = em.createQuery(
                            "select u from Utilisateur u where lower(u.username)=lower(:x)", Utilisateur.class)
                    .setParameter("x", username).setMaxResults(1).getResultList();
            return list.isEmpty()? Optional.empty() : Optional.of(list.get(0));
        } finally { em.close(); }
    }

    @Override public Optional<Utilisateur> findByEmail(String email) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            List<Utilisateur> list = em.createQuery(
                            "select u from Utilisateur u where lower(u.email)=lower(:x)", Utilisateur.class)
                    .setParameter("x", email).setMaxResults(1).getResultList();
            return list.isEmpty()? Optional.empty() : Optional.of(list.get(0));
        } finally { em.close(); }
    }
}
