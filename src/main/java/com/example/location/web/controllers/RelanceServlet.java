package com.example.location.web.controllers;

import com.example.location.config.JpaUtil;
import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Paiement;
import com.example.location.entity.PaiementStatut;
import com.example.location.security.Role;
import com.example.location.util.MailUtil;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/app/relances"})
public class RelanceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        if (user.getRole() == Role.LOCATAIRE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Réservé au propriétaire / admin");
            return;
        }

        EntityManager em = JpaUtil.getEntityManager();
        try {
            // MAJ des statuts selon échéance vs aujourd'hui
            JpaUtil.begin(em);
            LocalDate today = LocalDate.now();
            em.createQuery("""
                update Paiement p
                set p.statut = case
                    when p.datePaiement is not null then com.example.location.entity.PaiementStatut.PAYE
                    when p.echeance < :today then com.example.location.entity.PaiementStatut.RETARD
                    else com.example.location.entity.PaiementStatut.EN_ATTENTE
                end
            """).setParameter("today", today).executeUpdate();
            JpaUtil.commit(em);

            List<Paiement> late;
            if (user.getRole() == Role.ADMIN) {
                late = em.createQuery("""
                    select p from Paiement p
                      join fetch p.contrat c
                      join fetch c.locataire l
                      join fetch c.appartement a
                      join fetch a.immeuble i
                    where p.statut = :st
                    order by p.echeance asc
                """, Paiement.class)
                        .setParameter("st", PaiementStatut.RETARD)
                        .getResultList();
            } else { // PROPRIETAIRE : seulement ses immeubles
                late = em.createQuery("""
                    select p from Paiement p
                      join fetch p.contrat c
                      join fetch c.locataire l
                      join fetch c.appartement a
                      join fetch a.immeuble i
                    where p.statut = :st and i.proprietaire.id = :pid
                    order by p.echeance asc
                """, Paiement.class)
                        .setParameter("st", PaiementStatut.RETARD)
                        .setParameter("pid", user.getId())
                        .getResultList();
            }
            req.setAttribute("late", late);
            req.getRequestDispatcher("/WEB-INF/views/paiement/relances.jsp").forward(req, resp);
        } finally { em.close(); }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        if (user.getRole() == Role.LOCATAIRE) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Réservé au propriétaire / admin");
            return;
        }

        String action = trim(req.getParameter("action"));
        Long id = parseId(req.getParameter("id"));

        EntityManager em = JpaUtil.getEntityManager();
        try {
            switch (action) {
                case "relancer" -> {
                    Paiement p = em.find(Paiement.class, id);
                    if (p == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

                    String to = p.getContrat().getLocataire().getEmail();
                    String sujet = "Relance loyer en retard - échéance " + p.getEcheance();
                    String corps  = "Bonjour,\n\n"
                            + "Nous vous rappelons que le paiement du loyer d'un montant de "
                            + p.getMontant() + " € (échéance " + p.getEcheance()
                            + ") n'a pas encore été reçu.\n\nMerci de régulariser.\n";
                    MailUtil.send(to, sujet, corps);
                    resp.sendRedirect(req.getContextPath() + "/app/relances?ok=1");
                }
                case "relancerAll" -> {
                    // relancer tous les retards visibles
                    List<Paiement> list = (List<Paiement>) req.getSession().getAttribute("relances_cache");
                    if (list == null) {
                        resp.sendRedirect(req.getContextPath() + "/app/relances"); return;
                    }
                    for (Paiement p : list) {
                        String to = p.getContrat().getLocataire().getEmail();
                        String sujet = "Relance loyer - échéance " + p.getEcheance();
                        String corps  = "Bonjour, rappel : loyer " + p.getMontant()
                                + " € (échéance " + p.getEcheance() + ").";
                        MailUtil.send(to, sujet, corps);
                    }
                    resp.sendRedirect(req.getContextPath() + "/app/relances?ok=1");
                }
                default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } finally { em.close(); }
    }

    private String trim(String s){ return s==null? "": s.trim(); }
    private Long parseId(String raw){ try { return raw==null? null: Long.valueOf(raw.trim()); } catch(Exception e){ return null; } }
}
