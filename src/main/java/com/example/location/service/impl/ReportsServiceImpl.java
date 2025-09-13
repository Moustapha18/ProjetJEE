// src/main/java/com/example/location/service/impl/ReportsServiceImpl.java
package com.example.location.service.impl;

import com.example.location.config.JpaUtil;
import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;
import com.example.location.service.ReportsService;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class ReportsServiceImpl implements ReportsService {

    @Override
    public BigDecimal totalEncaisse(LocalDateTime fromTs, LocalDateTime toTs) {
        // Conversion vers LocalDate pour coller à Paiement.datePaiement (LocalDate)
        YearMonth ym = YearMonth.now();
        LocalDate from = (fromTs != null) ? fromTs.toLocalDate() : ym.atDay(1);
        LocalDate to   = (toTs   != null) ? toTs.toLocalDate()   : ym.atEndOfMonth();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select coalesce(sum(p.montant), 0)
                from Paiement p
                where p.datePaiement is not null
                  and p.datePaiement between :f and :t
            """, BigDecimal.class)
                    .setParameter("f", from) // LocalDate
                    .setParameter("t", to)   // LocalDate
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public BigDecimal totalAttendu(LocalDate from, LocalDate to) {
        // Bornes par défaut si null : mois courant
        YearMonth ym = YearMonth.now();
        if (from == null) from = ym.atDay(1);
        if (to   == null) to   = ym.atEndOfMonth();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                select coalesce(sum(p.montant), 0)
                from Paiement p
                where p.echeance between :f and :t
            """, BigDecimal.class)
                    .setParameter("f", from)
                    .setParameter("t", to)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Paiement> paiementsBetween(LocalDate from, LocalDate to, PaiementStatut statutOrNull) {
        // Bornes par défaut si null : mois courant
        YearMonth ym = YearMonth.now();
        if (from == null) from = ym.atDay(1);
        if (to   == null) to   = ym.atEndOfMonth();

        EntityManager em = JpaUtil.getEntityManager();
        try {
            String q = """
                select p from Paiement p
                join fetch p.contrat c
                join fetch c.locataire l
                join fetch c.appartement a
                join fetch a.immeuble i
                where p.echeance between :f and :t
            """;
            if (statutOrNull != null) {
                q += " and p.statut = :st";
            }
            q += " order by p.echeance desc, p.id desc";

            var query = em.createQuery(q, Paiement.class)
                    .setParameter("f", from)
                    .setParameter("t", to);

            if (statutOrNull != null) {
                query.setParameter("st", statutOrNull);
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
