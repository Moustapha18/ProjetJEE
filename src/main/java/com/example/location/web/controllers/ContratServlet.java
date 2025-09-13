package com.example.location.web.controllers;

import com.example.location.config.JpaUtil;
import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Contrat;
import com.example.location.security.Role;
import com.example.location.service.ContratService;
import com.example.location.service.impl.ContratServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/app/contrats"})
public class ContratServlet extends HttpServlet {

    private ContratService service;

    @Override public void init() { service = new ContratServiceImpl(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        var em = JpaUtil.getEntityManager();
        try {
            List<Contrat> list;
            if (user.getRole() == Role.ADMIN || user.getRole() == Role.PROPRIETAIRE) {
                list = em.createQuery("""
                    select c from Contrat c
                      join fetch c.appartement a
                      join fetch a.immeuble i
                      left join fetch c.locataire l
                    order by c.id desc
                """, Contrat.class).getResultList();
            } else { // LOCATAIRE : ne voit que ses contrats
                list = em.createQuery("""
                    select c from Contrat c
                      join fetch c.appartement a
                      join fetch a.immeuble i
                      left join fetch c.locataire l
                    where lower(l.email) = lower(:email)
                    order by c.id desc
                """, Contrat.class)
                        .setParameter("email", user.getEmail())
                        .getResultList();
            }
            req.setAttribute("contrats", list);
        } finally { em.close(); }

        req.getRequestDispatcher("/WEB-INF/views/contrat/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String action = trim(req.getParameter("action"));
        Long id = parseId(req.getParameter("id"));

        try {
            switch (action) {
                case "terminate" -> {
                    ensureOwnerOrAdmin(user, resp);
                    LocalDate fin = parseDate(req.getParameter("dateFin"));
                    service.terminate(id, fin);
                    resp.sendRedirect(req.getContextPath() + "/app/contrats");
                }
                case "renew" -> {
                    ensureOwnerOrAdmin(user, resp);
                    LocalDate start = parseDate(req.getParameter("dateDebut"));
                    BigDecimal newLoyer = parseDecimal(req.getParameter("loyer"));
                    service.renew(id, start, newLoyer);

                    LocalDate from = (start != null ? start : LocalDate.now()).withDayOfMonth(1);
                    LocalDate to   = from.plusMonths(11).withDayOfMonth(from.plusMonths(11).lengthOfMonth());
                    service.ensureSchedule(id, from, to);

                    resp.sendRedirect(req.getContextPath() + "/app/contrats");
                }
                case "gensched" -> {
                    ensureOwnerOrAdmin(user, resp);
                    LocalDate from = parseDate(req.getParameter("from"));
                    LocalDate to   = parseDate(req.getParameter("to"));
                    if (from == null) from = LocalDate.now().withDayOfMonth(1);
                    if (to == null)   to   = from.plusMonths(5).withDayOfMonth(from.plusMonths(5).lengthOfMonth());
                    service.ensureSchedule(id, from, to);
                    resp.sendRedirect(req.getContextPath() + "/app/contrats");
                }
                default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            req.setAttribute("error", ex.getMessage());
            doGet(req, resp);
        }
    }

    // --- guards & helpers
    private void ensureOwnerOrAdmin(UtilisateurDto u, HttpServletResponse resp) throws IOException {
        if (u.getRole() != Role.ADMIN && u.getRole() != Role.PROPRIETAIRE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Action réservée au propriétaire / admin");
        }
    }
    private String trim(String s){ return s==null? "": s.trim(); }
    private Long parseId(String raw){ try { return raw==null? null: Long.valueOf(raw.trim()); } catch(Exception e){ return null; } }
    private BigDecimal parseDecimal(String raw){ try { return raw==null||raw.isBlank()? null: new BigDecimal(raw.trim()); } catch(Exception e){ return null; } }
    private LocalDate parseDate(String raw){ try { return raw==null||raw.isBlank()? null: LocalDate.parse(raw.trim()); } catch(Exception e){ return null; } }
}
