package com.example.location.web.controllers;

import com.example.location.config.JpaUtil;
import com.example.location.entity.Contrat;
import com.example.location.entity.StatutContrat;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/app"})
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        var em = JpaUtil.getEntityManager();
        try {
            // Bornes du mois (LocalDate partout)
            LocalDate today    = LocalDate.now();
            LocalDate in30d    = today.plusDays(30);
            YearMonth ymNow    = YearMonth.now();
            LocalDate firstDay = ymNow.atDay(1);
            LocalDate lastDay  = ymNow.atEndOfMonth();

            // 1) Contrats qui expirent sous 30 jours (dateFin = LocalDate)
            List<Contrat> contratsQuiExpirent = em.createQuery(
                            "select c from Contrat c " +
                                    "where c.statut = :actif and c.dateFin is not null " +
                                    "and c.dateFin between :d1 and :d2",
                            Contrat.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .setParameter("d1", today)
                    .setParameter("d2", in30d)
                    .getResultList();

            // 2) Contrats actifs sans paiement ce mois (datePaiement = LocalDate)
            List<Contrat> contratsEnRetard = em.createQuery(
                            "select c from Contrat c " +
                                    "where c.statut = :actif and c.dateFin is null and not exists (" +
                                    "  select p.id from Paiement p " +
                                    "  where p.contrat = c and p.datePaiement between :m1 and :m2" +
                                    ")",
                            Contrat.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .setParameter("m1", firstDay)
                    .setParameter("m2", lastDay)
                    .getResultList();

            // KPIs
            long nbImmeubles = em.createQuery("select count(i) from Immeuble i", Long.class).getSingleResult();
            long nbAppartements = em.createQuery("select count(a) from Appartement a", Long.class).getSingleResult();

            long nbAppartsOccupes = em.createQuery(
                            "select count(distinct c.appartement.id) from Contrat c " +
                                    "where c.statut = :actif and c.dateFin is null",
                            Long.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .getSingleResult();

            long nbAppartsLibres = nbAppartements - nbAppartsOccupes;

            long nbLocataires = em.createQuery("select count(l) from Locataire l", Long.class).getSingleResult();

            long nbContratsActifs = em.createQuery(
                            "select count(c) from Contrat c " +
                                    "where c.statut = :actif and c.dateFin is null",
                            Long.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .getSingleResult();

            BigDecimal loyerMensuelTotal = em.createQuery(
                            "select coalesce(sum(c.loyerMensuel), 0) from Contrat c " +
                                    "where c.statut = :actif and c.dateFin is null",
                            BigDecimal.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .getSingleResult();

            // Encaissements du mois (datePaiement = LocalDate)
            BigDecimal paiementsMois = em.createQuery(
                            "select coalesce(sum(p.montant), 0) from Paiement p " +
                                    "where p.datePaiement between :m1 and :m2",
                            BigDecimal.class)
                    .setParameter("m1", firstDay)
                    .setParameter("m2", lastDay)
                    .getSingleResult();

            req.setAttribute("contratsQuiExpirent", contratsQuiExpirent);
            req.setAttribute("contratsEnRetard",   contratsEnRetard);
            req.setAttribute("nbImmeubles",        nbImmeubles);
            req.setAttribute("nbAppartements",     nbAppartements);
            req.setAttribute("nbAppartsOccupes",   nbAppartsOccupes);
            req.setAttribute("nbAppartsLibres",    nbAppartsLibres);
            req.setAttribute("nbLocataires",       nbLocataires);
            req.setAttribute("nbContratsActifs",   nbContratsActifs);
            req.setAttribute("loyerMensuelTotal",  loyerMensuelTotal);
            req.setAttribute("paiementsMois",      paiementsMois);
            req.setAttribute("periodeMois",
                    ymNow.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + ymNow.getYear());

            // Série "paiements encaissés - 6 derniers mois" (LocalDate)
            List<String> labels = new ArrayList<>();
            List<BigDecimal> series = new ArrayList<>();
            for (int i = 5; i >= 0; i--) {
                YearMonth ym = ymNow.minusMonths(i);
                LocalDate s = ym.atDay(1);
                LocalDate e = ym.atEndOfMonth();

                BigDecimal sum = em.createQuery(
                                "select coalesce(sum(p.montant), 0) from Paiement p " +
                                        "where p.datePaiement between :s and :e",
                                BigDecimal.class)
                        .setParameter("s", s)
                        .setParameter("e", e)
                        .getSingleResult();

                labels.add(ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + ym.getYear());
                series.add(sum);
            }
            req.setAttribute("chartLabels", toJson(labels));
            req.setAttribute("chartData",   toJson(series));

            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }

    private static String toJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof String s) sb.append("\"").append(s.replace("\"", "\\\"")).append("\"");
            else sb.append(o == null ? "null" : o.toString());
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
