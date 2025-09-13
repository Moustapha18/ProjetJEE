// src/main/java/com/example/location/web/controllers/PaiementServlet.java
package com.example.location.web.controllers;

import com.example.location.config.JpaUtil;
import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Paiement;
import com.example.location.security.Role;
import com.example.location.service.PaiementService;
import com.example.location.service.impl.PaiementServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/app/paiements"})
public class PaiementServlet extends HttpServlet {

    private PaiementService service;

    @Override public void init() { service = new PaiementServiceImpl(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String action = trim(req.getParameter("action"));
        Long id = parseId(req.getParameter("id"));

        if ("recu".equals(action)) {
            var p = service.findById(id).orElse(null);
            if (p == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            req.setAttribute("p", p);
            req.getRequestDispatcher("/WEB-INF/views/paiement/recu.jsp").forward(req, resp);
            return;
        }

        // Met à jour EN_ATTENTE / RETARD / PAYE
        service.refreshStatuses();

        var em = JpaUtil.getEntityManager();
        try {
            List<Paiement> list;
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.PROPRIETAIRE) {
                list = em.createQuery("""
                    select p from Paiement p
                      join fetch p.contrat c
                      join fetch c.appartement a
                      join fetch a.immeuble i
                      left  join fetch c.locataire l
                    order by p.echeance desc, p.id desc
                """, Paiement.class).getResultList();
            } else { // LOCATAIRE
                list = em.createQuery("""
                    select p from Paiement p
                      join fetch p.contrat c
                      join fetch c.appartement a
                      join fetch a.immeuble i
                      join fetch c.locataire l
                    where lower(l.email) = lower(:email)
                    order by p.echeance desc, p.id desc
                """, Paiement.class)
                        .setParameter("email", user.getEmail())
                        .getResultList();

                // ✅ Backfill : si aucune ligne, on génère les échéances pour ses contrats actifs puis on recharge
                if (list == null || list.isEmpty()) {
                    var svc = new com.example.location.service.impl.PaiementServiceImpl();

                    var contrats = em.createQuery("""
                        select c from Contrat c
                          join c.locataire l
                        where lower(l.email) = lower(:em)
                          and c.dateFin is null
                    """, com.example.location.entity.Contrat.class)
                            .setParameter("em", user.getEmail())
                            .getResultList();

                    for (var c : contrats) {
                        var start = c.getDateDebut() != null ? c.getDateDebut() : LocalDate.now();
                        svc.ensureScheduleForContract(c.getId(), start, start.plusMonths(12));
                    }

                    list = em.createQuery("""
                        select p from Paiement p
                          join fetch p.contrat c
                          join fetch c.appartement a
                          join fetch a.immeuble i
                          join fetch c.locataire l
                        where lower(l.email) = lower(:email)
                        order by p.echeance desc, p.id desc
                    """, Paiement.class)
                            .setParameter("email", user.getEmail())
                            .getResultList();
                }
            }

            req.setAttribute("paiements", list);
            req.getRequestDispatcher("/WEB-INF/views/paiement/list.jsp").forward(req, resp);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String action = trim(req.getParameter("action"));

        try {
            switch (action) {
                // ✅ Paiement par le locataire
                case "pay" -> {
                    Long id = parseId(req.getParameter("id"));
                    String reference = trim(req.getParameter("reference"));

                    var p = service.findById(id).orElse(null);
                    if (p == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

                    if (user.getRole() == Role.LOCATAIRE) {
                        var loc = p.getContrat().getLocataire();
                        String locMail = (loc != null ? loc.getEmail() : null);
                        if (locMail == null || user.getEmail() == null
                                || !locMail.equalsIgnoreCase(user.getEmail())) {
                            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous ne pouvez payer que vos échéances.");
                            return;
                        }
                    }

                    service.markPaid(id, reference, LocalDate.now());
                    resp.sendRedirect(req.getContextPath() + "/app/paiements?action=recu&id=" + id);
                }

                // Validation manuelle (proprio/admin)
                case "markPaid" -> {
                    if (user.getRole() == Role.LOCATAIRE) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Réservé au propriétaire / admin");
                        return;
                    }
                    Long id = parseId(req.getParameter("id"));
                    String reference = trim(req.getParameter("reference"));
                    LocalDate paidAt = parseDate(req.getParameter("datePaiement"));
                    service.markPaid(id, reference, paidAt);
                    resp.sendRedirect(req.getContextPath() + "/app/paiements");
                }

                default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (RuntimeException ex) {
            req.setAttribute("error", ex.getMessage());
            doGet(req, resp);
        }
    }

    // helpers
    private String trim(String s){ return s==null? "": s.trim(); }
    private Long parseId(String raw){ try { return raw==null? null: Long.valueOf(raw.trim()); } catch(Exception e){ return null; } }
    private LocalDate parseDate(String raw){ try { return raw==null||raw.isBlank()? null: LocalDate.parse(raw.trim()); } catch(Exception e){ return null; } }
}
