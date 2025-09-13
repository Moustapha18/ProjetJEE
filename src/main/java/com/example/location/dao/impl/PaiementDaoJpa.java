package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.PaiementDao;
import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PaiementDaoJpa implements PaiementDao {

    @Override
    public Paiement save(Paiement p) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            em.persist(p);
            JpaUtil.commit(em);
            return p;
        } catch (Exception e) {
            JpaUtil.rollback(em);
            throw e;
        } finally { em.close(); }
    }

    @Override
    public Paiement update(Paiement p) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Paiement merged = em.merge(p);
            JpaUtil.commit(em);
            return merged;
        } catch (Exception e) {
            JpaUtil.rollback(em);
            throw e;
        } finally { em.close(); }
    }

    @Override
    public Optional<Paiement> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            var list = em.createQuery("""
                select p from Paiement p
                join fetch p.contrat c
                join fetch c.locataire l
                join fetch c.appartement a
                join fetch a.immeuble i
                where p.id = :id
            """, Paiement.class)
                    .setParameter("id", id)
                    .getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally { em.close(); }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Paiement ref = em.find(Paiement.class, id);
            if (ref != null) em.remove(ref);
            JpaUtil.commit(em);
        } catch (Exception e) {
            JpaUtil.rollback(em);
            throw e;
        } finally { em.close(); }
    }

    @Override
    public List<Paiement> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select p from Paiement p
                join fetch p.contrat c
                join fetch c.appartement a
                join fetch a.immeuble i
                order by p.echeance desc, p.id desc
            """, Paiement.class).getResultList();
        } finally { em.close(); }
    }

    @Override
    public List<Paiement> findByContratId(Long contratId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select p from Paiement p
                join fetch p.contrat c
                join fetch c.appartement a
                join fetch a.immeuble i
                where c.id = :cid
                order by p.echeance desc, p.id desc
            """, Paiement.class)
                    .setParameter("cid", contratId)
                    .getResultList();
        } finally { em.close(); }
    }

    @Override
    public List<Paiement> findByLocataireEmail(String email) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select p from Paiement p
                join fetch p.contrat c
                join fetch c.locataire l
                join fetch c.appartement a
                join fetch a.immeuble i
                where lower(l.email) = lower(:em)
                order by p.echeance desc, p.id desc
            """, Paiement.class)
                    .setParameter("em", email)
                    .getResultList();
        } finally { em.close(); }
    }

    @Override
    public Optional<Paiement> findByContratAndEcheance(Long contratId, LocalDate echeance) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            var list = em.createQuery("""
                select p from Paiement p
                where p.contrat.id = :cid and p.echeance = :ech
            """, Paiement.class)
                    .setParameter("cid", contratId)
                    .setParameter("ech", echeance)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally { em.close(); }
    }

    @Override
    public List<Paiement> findByStatut(PaiementStatut statut) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select p from Paiement p
                where p.statut = :st
                order by p.echeance desc
            """, Paiement.class)
                    .setParameter("st", statut)
                    .getResultList();
        } finally { em.close(); }
    }
}
