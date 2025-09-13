// src/main/java/com/example/location/service/impl/ContratServiceImpl.java
package com.example.location.service.impl;

import com.example.location.config.JpaUtil;
import com.example.location.entity.Contrat;
import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;
import com.example.location.entity.StatutContrat;
import com.example.location.service.ContratService;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

public class ContratServiceImpl implements ContratService {

    @Override
    public Optional<Contrat> findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Contrat.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Contrat terminate(Long contratId, LocalDate endDate) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Contrat c = em.find(Contrat.class, contratId);
            if (c == null) throw new IllegalArgumentException("Contrat introuvable");

            c.setDateFin(endDate != null ? endDate : LocalDate.now());
            c.setStatut(resolveInactif());

            Contrat merged = em.merge(c);
            JpaUtil.commit(em);
            return merged;
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em);
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public Contrat renew(Long contratId, LocalDate newStartDate, BigDecimal newLoyer) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Contrat c = em.find(Contrat.class, contratId);
            if (c == null) throw new IllegalArgumentException("Contrat introuvable");

            c.setStatut(resolveActif());
            c.setDateFin(null);
            if (newStartDate != null) c.setDateDebut(newStartDate);
            if (newLoyer != null && newLoyer.signum() > 0) c.setLoyerMensuel(newLoyer);

            Contrat merged = em.merge(c);
            JpaUtil.commit(em);
            return merged;
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em);
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void ensureSchedule(Long contratId, LocalDate startInclusive, LocalDate endInclusive) {
        if (startInclusive == null || endInclusive == null || endInclusive.isBefore(startInclusive)) {
            throw new IllegalArgumentException("Période invalide");
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Contrat c = em.find(Contrat.class, contratId);
            if (c == null) throw new IllegalArgumentException("Contrat introuvable");
            if (c.getLoyerMensuel() == null) throw new IllegalStateException("Loyer du contrat manquant");

            YearMonth ym = YearMonth.from(startInclusive);
            YearMonth stop = YearMonth.from(endInclusive);

            while (!ym.isAfter(stop)) {
                LocalDate due = ym.atEndOfMonth(); // échéance = fin de mois

                // Éviter les doublons d'échéance
                Long exists = em.createQuery("""
                    select count(p) from Paiement p
                    where p.contrat.id = :cid and p.echeance = :due
                """, Long.class)
                        .setParameter("cid", contratId)
                        .setParameter("due", due)
                        .getSingleResult();

                if (exists == null || exists == 0L) {
                    Paiement p = new Paiement();
                    p.setContrat(c);
                    p.setEcheance(due);
                    p.setMontant(c.getLoyerMensuel());
                    p.setStatut(PaiementStatut.EN_ATTENTE);
                    em.persist(p);
                }

                ym = ym.plusMonths(1);
            }

            JpaUtil.commit(em);
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em);
            throw ex;
        } finally {
            em.close();
        }
    }

    // --- Helpers: tolérance aux variantes de noms d'énum
    private StatutContrat resolveActif() {
        String[] cands = {"ACTIF", "EN_COURS", "ENCOURS", "ACTIVE"};
        for (String s : cands) {
            for (StatutContrat sc : StatutContrat.values()) {
                if (sc.name().replace("_", "").equalsIgnoreCase(s.replace("_", ""))) {
                    return sc;
                }
            }
        }
        return StatutContrat.values()[0];
    }

    private StatutContrat resolveInactif() {
        String[] cands = {"RESILIE", "INACTIF", "TERMINE", "CLOS"};
        for (String s : cands) {
            for (StatutContrat sc : StatutContrat.values()) {
                if (sc.name().replace("_", "").equalsIgnoreCase(s.replace("_", ""))) {
                    return sc;
                }
            }
        }
        return StatutContrat.values()[0];
    }
}
