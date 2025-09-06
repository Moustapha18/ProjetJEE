package com.example.location.web.controllers;// ...
import com.example.location.config.JpaUtil;
import com.example.location.entity.StatutContrat;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;




import java.io.IOException;
import java.time.LocalDate;
// supprime les imports LocalDateTime si non utilisés

@WebServlet(name = "DashboardServlet", urlPatterns = {"/app"})
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        var em = JpaUtil.getEntityManager();
        try {
            // bornes du mois en LocalDate (PAS LocalDateTime)
            LocalDate today = LocalDate.now();
            LocalDate in30d = today.plus(30, ChronoUnit.DAYS);
            LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDay  = firstDay.plusMonths(1).minusDays(1);

            List<com.example.location.entity.Contrat> contratsQuiExpirent = em.createQuery(
                            "select c from Contrat c " +
                                    "where c.statut = :actif " +
                                    "and c.dateFin is not null " +
                                    "and c.dateFin between :today and :in30", com.example.location.entity.Contrat.class)
                    .setParameter("actif", com.example.location.entity.StatutContrat.ACTIF)
                    .setParameter("today", today)
                    .setParameter("in30", in30d)
                    .getResultList();

// 2) Contrats actifs sans paiement ce mois (retards)
            List<com.example.location.entity.Contrat> contratsEnRetard = em.createQuery(
                            "select c from Contrat c " +
                                    "where c.statut = :actif " +
                                    "and not exists (" +
                                    "  select p.id from Paiement p " +
                                    "  where p.contrat = c and p.datePaiement between :start and :end" +
                                    ")",
                            com.example.location.entity.Contrat.class)
                    .setParameter("actif", com.example.location.entity.StatutContrat.ACTIF)
                    .setParameter("start", firstDay)
                    .setParameter("end", lastDay)
                    .getResultList();

            req.setAttribute("contratsQuiExpirent", contratsQuiExpirent);
            req.setAttribute("contratsEnRetard", contratsEnRetard);

            long nbImmeubles = em.createQuery("select count(i) from Immeuble i", Long.class)
                    .getSingleResult();

            long nbAppartements = em.createQuery("select count(a) from Appartement a", Long.class)
                    .getSingleResult();

            long nbAppartsOccupes = em.createQuery(
                            "select count(distinct c.appartement.id) from Contrat c where c.statut = :actif", Long.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .getSingleResult();

            long nbAppartsLibres = nbAppartements - nbAppartsOccupes;

            long nbLocataires = em.createQuery("select count(l) from Locataire l", Long.class)
                    .getSingleResult();

            long nbContratsActifs = em.createQuery(
                            "select count(c) from Contrat c where c.statut = :actif", Long.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .getSingleResult();

            BigDecimal loyerMensuelTotal = em.createQuery(
                            "select coalesce(sum(c.loyerMensuel), 0) from Contrat c where c.statut = :actif", BigDecimal.class)
                    .setParameter("actif", StatutContrat.ACTIF)
                    .getSingleResult();

            // ICI: utiliser LocalDate pour matcher le type de p.datePaiement
            BigDecimal paiementsMois = em.createQuery(
                            "select coalesce(sum(p.montant), 0) " +
                                    "from Paiement p " +
                                    "where p.datePaiement between :start and :end", BigDecimal.class)
                    .setParameter("start", firstDay)
                    .setParameter("end", lastDay)
                    .getSingleResult();

            req.setAttribute("nbImmeubles", nbImmeubles);
            req.setAttribute("nbAppartements", nbAppartements);
            req.setAttribute("nbAppartsOccupes", nbAppartsOccupes);
            req.setAttribute("nbAppartsLibres", nbAppartsLibres);
            req.setAttribute("nbLocataires", nbLocataires);
            req.setAttribute("nbContratsActifs", nbContratsActifs);
            req.setAttribute("loyerMensuelTotal", loyerMensuelTotal);
            req.setAttribute("paiementsMois", paiementsMois);
            req.setAttribute("periodeMois", firstDay.getMonth() + " " + firstDay.getYear());
            // --- Série "paiements encaissés - 6 derniers mois"
            List<String> mLabels = new ArrayList<>();
            List<BigDecimal> mData = new ArrayList<>();

            for (int i = 5; i >= 0; i--) {
                YearMonth ym = YearMonth.now().minusMonths(i);
                LocalDate mStart = ym.atDay(1);
                LocalDate mEnd   = ym.atEndOfMonth();

                BigDecimal sum = em.createQuery(
                                "select coalesce(sum(p.montant), 0) " +
                                        "from Paiement p where p.datePaiement between :s and :e",
                                BigDecimal.class)
                        .setParameter("s", mStart)
                        .setParameter("e", mEnd)
                        .getSingleResult();

                String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + ym.getYear();
                mLabels.add(label);
                mData.add(sum);
            }

// Expose en JSON pour la JSP
            req.setAttribute("chartLabels", toJson(mLabels));
            req.setAttribute("chartData",   toJson(mData));


            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }
    private static String toJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof String s) {
                sb.append("\"").append(s.replace("\"", "\\\"")).append("\"");
            } else {
                sb.append(o == null ? "null" : o.toString());
            }
            if (i < list.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }


}
