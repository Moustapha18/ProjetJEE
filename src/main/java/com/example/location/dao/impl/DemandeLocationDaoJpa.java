// src/main/java/com/example/location/dao/impl/DemandeLocationDaoJpa.java
package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.DemandeLocationDao;
import com.example.location.entity.DemandeLocation;
import com.example.location.entity.DemandeStatut;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class DemandeLocationDaoJpa implements DemandeLocationDao {

    @Override
    public DemandeLocation save(DemandeLocation d) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            em.persist(d);
            JpaUtil.commit(em);
            return d;
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em);
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public DemandeLocation update(DemandeLocation d) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            DemandeLocation m = em.merge(d);
            JpaUtil.commit(em);
            return m;
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em);
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<DemandeLocation> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<DemandeLocation> list = em.createQuery("""
                select distinct d from DemandeLocation d
                  join fetch d.appartement a
                  join fetch a.immeuble i
                  join fetch d.locataire u
                where d.id = :id
            """, DemandeLocation.class)
                    .setParameter("id", id)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public List<DemandeLocation> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select distinct d from DemandeLocation d
                  join fetch d.appartement a
                  join fetch a.immeuble i
                  join fetch d.locataire u
                order by d.id desc
            """, DemandeLocation.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<DemandeLocation> findByLocataireId(Long userId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select distinct d from DemandeLocation d
                  join fetch d.appartement a
                  join fetch a.immeuble i
                  join fetch d.locataire u
                where u.id = :uid
                order by d.id desc
            """, DemandeLocation.class)
                    .setParameter("uid", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<DemandeLocation> findByProprietaireId(Long proprietaireId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // ⚠️ Assure-toi que Immeuble possède bien le champ `proprietaire` (ManyToOne Utilisateur).
            return em.createQuery("""
                select distinct d from DemandeLocation d
                  join fetch d.appartement a
                  join fetch a.immeuble i
                  join fetch d.locataire u
                where i.proprietaire.id = :pid
                order by d.id desc
            """, DemandeLocation.class)
                    .setParameter("pid", proprietaireId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<DemandeLocation> findPendingByAppAndUser(Long appartementId, Long userId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<DemandeLocation> list = em.createQuery("""
                select d from DemandeLocation d
                  join d.appartement a
                  join d.locataire u
                where a.id = :aid
                  and u.id = :uid
                  and d.statut = :att
                order by d.id desc
            """, DemandeLocation.class)
                    .setParameter("aid", appartementId)
                    .setParameter("uid", userId)
                    .setParameter("att", DemandeStatut.EN_ATTENTE)
                    .setMaxResults(1)
                    .getResultList();
            return list.stream().findFirst();
        } finally {
            em.close();
        }
    }
}
