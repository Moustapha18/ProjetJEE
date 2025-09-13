package com.example.location.dao.impl;

import com.example.location.config.JpaUtil;
import com.example.location.dao.AppartementDao;
import com.example.location.entity.Appartement;
import com.example.location.entity.Immeuble;
import com.example.location.entity.StatutContrat;
import com.example.location.entity.DemandeStatut;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Locale;

public class AppartementDaoJpa implements AppartementDao {

    public AppartementDaoJpa() { }

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
            Appartement a = em.find(Appartement.class, id);
            if (a != null) {
                applyFlags(em, Collections.singletonList(a));
            }
            return Optional.ofNullable(a);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Appartement> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Appartement> list = em.createQuery("""
                select a from Appartement a
                join fetch a.immeuble i
                order by a.id desc
            """, Appartement.class).getResultList();
            applyFlags(em, list);
            return list;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Appartement> findByImmeubleId(Long immeubleId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Appartement> list = em.createQuery("""
                select a from Appartement a
                join fetch a.immeuble i
                where i.id = :id
                order by a.id desc
            """, Appartement.class)
                    .setParameter("id", immeubleId)
                    .getResultList();
            applyFlags(em, list);
            return list;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Appartement> searchFiltered(BigDecimal min, BigDecimal max, String ville, Integer nbPieces) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Appartement> cq = cb.createQuery(Appartement.class);
            Root<Appartement> root = cq.from(Appartement.class);
            Join<Appartement, Immeuble> im = root.join("immeuble");

            List<Predicate> ps = new ArrayList<>();
            if (min != null) ps.add(cb.ge(root.get("loyer"), min));
            if (max != null) ps.add(cb.le(root.get("loyer"), max));
            if (nbPieces != null) ps.add(cb.equal(root.get("nbPieces"), nbPieces));
            if (ville != null && !ville.isBlank()) {
                String v = "%" + ville.toLowerCase(Locale.ROOT).trim() + "%";
                ps.add(cb.like(cb.lower(im.get("ville")), v));
            }

            cq.select(root).where(ps.toArray(Predicate[]::new));
            cq.orderBy(cb.asc(root.get("loyer")), cb.asc(root.get("id")));

            List<Appartement> list = em.createQuery(cq).getResultList();
            // force load immeuble si nécessaire
            list.forEach(a -> { if (a.getImmeuble()!=null) a.getImmeuble().getNom(); });
            applyFlags(em, list);
            return list;
        } finally {
            em.close();
        }
    }

    /** Renseigne a.occupe (contrat ACTIF sans fin) et a.reserve (demande EN_ATTENTE) */
    private void applyFlags(EntityManager em, List<Appartement> list) {
        if (list == null || list.isEmpty()) return;

        Set<Long> ids = list.stream()
                .map(Appartement::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) return;

        // Occupés : contrat actif
        List<Long> occIds = em.createQuery("""
            select distinct c.appartement.id from Contrat c
            where c.statut = :actif and c.dateFin is null and c.appartement.id in :ids
        """, Long.class)
                .setParameter("actif", StatutContrat.ACTIF)
                .setParameter("ids", ids)
                .getResultList();
        Set<Long> occupied = new HashSet<>(occIds);

        // Réservés : au moins une demande EN_ATTENTE
        List<Long> resIds = em.createQuery("""
            select distinct d.appartement.id from DemandeLocation d
            where d.statut = :att and d.appartement.id in :ids
        """, Long.class)
                .setParameter("att", DemandeStatut.EN_ATTENTE)
                .setParameter("ids", ids)
                .getResultList();
        Set<Long> reserved = new HashSet<>(resIds);

        for (Appartement a : list) {
            Long id = a.getId();
            if (id == null) continue;
            a.setOccupe(occupied.contains(id));
            a.setReserve(reserved.contains(id));
        }
    }
}
