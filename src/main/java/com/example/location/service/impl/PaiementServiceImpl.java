package com.example.location.service.impl;

import com.example.location.config.JpaUtil;
import com.example.location.entity.Contrat;
import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;
import com.example.location.service.PaiementService;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public class PaiementServiceImpl implements PaiementService {

    @Override
    public List<Paiement> findAll() {
        var em = JpaUtil.getEntityManager();
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
    public Optional<Paiement> findById(Long id) {
        var em = JpaUtil.getEntityManager();
        try {
            var list = em.createQuery("""
                select p from Paiement p
                  join fetch p.contrat c
                  left join fetch c.locataire l
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
    public void refreshStatuses() {
        var em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            LocalDate today = LocalDate.now();

            List<Paiement> all = em.createQuery("select p from Paiement p", Paiement.class).getResultList();
            for (Paiement p : all) {
                if (p.getDatePaiement() != null) {
                    p.setStatut(PaiementStatut.PAYE);
                } else if (p.getEcheance() != null && p.getEcheance().isBefore(today)) {
                    p.setStatut(PaiementStatut.RETARD);
                } else {
                    p.setStatut(PaiementStatut.EN_ATTENTE);
                }
            }
            JpaUtil.commit(em);
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em); throw ex;
        } finally { em.close(); }
    }

    @Override
    public void generateScheduleForContract(Contrat c, int months) {
        if (c == null || c.getId() == null) return;
        LocalDate start = c.getDateDebut() != null ? c.getDateDebut() : LocalDate.now();
        YearMonth ymStart = YearMonth.from(start);
        YearMonth ymEnd   = ymStart.plusMonths(Math.max(0, months - 1));
        LocalDate end = ymEnd.atEndOfMonth();
        ensureScheduleForContract(c.getId(), start, end);
    }

    @Override
    public void ensureScheduleForContract(Long contratId, LocalDate startInclusive, LocalDate endInclusive) {
        if (contratId == null || startInclusive == null || endInclusive == null) return;

        var em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);

            Contrat c = em.find(Contrat.class, contratId);
            if (c == null) throw new IllegalArgumentException("Contrat introuvable");

            int day = (c.getDateDebut() != null) ? c.getDateDebut().getDayOfMonth() : 1;

            YearMonth ymStart = YearMonth.from(startInclusive);
            YearMonth ymEnd   = YearMonth.from(endInclusive);

            YearMonth ym = ymStart;
            while (!ym.isAfter(ymEnd)) {
                int d = Math.min(day, ym.lengthOfMonth());
                LocalDate due = LocalDate.of(ym.getYear(), ym.getMonth(), d);

                Long count = em.createQuery("""
                    select count(p) from Paiement p
                    where p.contrat.id = :cid and p.echeance = :ech
                """, Long.class)
                        .setParameter("cid", contratId)
                        .setParameter("ech", due)
                        .getSingleResult();

                if (count == 0) {
                    Paiement p = new Paiement();
                    p.setContrat(c);
                    p.setMontant(c.getLoyerMensuel());
                    p.setEcheance(due);
                    p.setStatut(PaiementStatut.EN_ATTENTE);
                    em.persist(p);
                }
                ym = ym.plusMonths(1);
            }

            JpaUtil.commit(em);
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em); throw ex;
        } finally { em.close(); }
    }

    @Override
    public Paiement markPaid(Long paiementId, String reference, LocalDate paidAt) {
        var em = JpaUtil.getEntityManager();
        try {
            JpaUtil.begin(em);
            Paiement p = em.find(Paiement.class, paiementId);
            if (p == null) throw new IllegalArgumentException("Paiement introuvable");

            p.setDatePaiement(paidAt != null ? paidAt : LocalDate.now());
            p.setReference(reference != null && !reference.isBlank() ? reference.trim() : null);
            p.setStatut(PaiementStatut.PAYE);

            Paiement merged = em.merge(p);
            JpaUtil.commit(em);
            return merged;
        } catch (RuntimeException ex) {
            JpaUtil.rollback(em); throw ex;
        } finally { em.close(); }
    }
}
