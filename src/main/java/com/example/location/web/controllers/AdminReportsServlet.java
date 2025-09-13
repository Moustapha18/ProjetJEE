package com.example.location.web.controllers;

import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;
import com.example.location.security.Role;
import com.example.location.service.ReportsService;
import com.example.location.service.impl.ReportsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@WebServlet(urlPatterns = {"/app/reports"})
public class AdminReportsServlet extends HttpServlet {

    private ReportsService service;

    @Override public void init() { service = new ReportsServiceImpl(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null || user.getRole() != Role.ADMIN) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé à l'administrateur");
            return;
        }

        String action = param(req, "action");
        LocalDate from = parseDate(req.getParameter("from"));
        LocalDate to   = parseDate(req.getParameter("to"));
        if (from == null) from = LocalDate.now().withDayOfMonth(1);
        if (to   == null) to   = LocalDate.now();

        // Pour l'encaissement: p.datePaiement = LocalDateTime
        LocalDateTime fromTs = from.atStartOfDay();
        LocalDateTime toTs   = to.atTime(LocalTime.MAX);

        PaiementStatut statut = parseStatut(param(req, "statut"));

        if ("export".equals(action)) {
            exportCsv(resp, from, to, statut);
            return;
        }

        BigDecimal encaisse = service.totalEncaisse(fromTs, toTs);
        BigDecimal attendu  = service.totalAttendu(from, to);
        BigDecimal reste    = attendu.subtract(encaisse);

        List<Paiement> lignes = service.paiementsBetween(from, to, statut);

        req.setAttribute("from", from);
        req.setAttribute("to", to);
        req.setAttribute("statut", statut == null ? "" : statut.name());
        req.setAttribute("encaisse", encaisse);
        req.setAttribute("attendu",  attendu);
        req.setAttribute("reste",    reste);
        req.setAttribute("lignes",   lignes);

        req.getRequestDispatcher("/WEB-INF/views/reports/reports.jsp").forward(req, resp);
    }

    private void exportCsv(HttpServletResponse resp, LocalDate from, LocalDate to, PaiementStatut statut)
            throws IOException {

        List<Paiement> rows = service.paiementsBetween(from, to, statut);

        String filename = "rapport-paiements_" + from + "_au_" + to + (statut != null ? "_" + statut : "") + ".csv";
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (PrintWriter out = resp.getWriter()) {
            out.println("PaiementID;Echeance;Montant;Statut;DatePaiement;Ref;ContratID;Locataire;Email;Immeuble;Ville;Appartement");
            for (Paiement p : rows) {
                out.printf("%d;%s;%s;%s;%s;%s;%d;%s;%s;%s;%s;%s%n",
                        p.getId(),
                        p.getEcheance(),
                        p.getMontant(),
                        p.getStatut(),
                        p.getDatePaiement() != null ? p.getDatePaiement() : "",
                        p.getReference() != null ? p.getReference() : "",
                        p.getContrat().getId(),
                        safe(p.getContrat().getLocataire().getNomComplet()),
                        safe(p.getContrat().getLocataire().getEmail()),
                        safe(p.getContrat().getAppartement().getImmeuble().getNom()),
                        safe(p.getContrat().getAppartement().getImmeuble().getVille()),
                        safe(p.getContrat().getAppartement().getNumero())
                );
            }
        }
    }

    private String safe(String s){ return s == null ? "" : s.replace(";", ","); }
    private String param(HttpServletRequest req, String name){ String v=req.getParameter(name); return v==null?"":v.trim(); }
    private LocalDate parseDate(String raw){ try{ return (raw==null||raw.isBlank())?null:LocalDate.parse(raw.trim()); }catch(Exception e){ return null; } }
    private PaiementStatut parseStatut(String s){ try{ return (s==null||s.isBlank())?null:PaiementStatut.valueOf(s.trim()); }catch(Exception e){ return null; } }
}
