package com.example.location.web.controllers;

import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.DemandeLocation;
import com.example.location.entity.DemandeStatut;
import com.example.location.security.Role;
import com.example.location.service.DemandeLocationService;
import com.example.location.service.impl.DemandeLocationServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/app/demandes"})
public class DemandeServlet extends HttpServlet {

    private DemandeLocationService service;

    @Override public void init() { service = new DemandeLocationServiceImpl(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        List<DemandeLocation> list;
        Role r = user.getRole();
        switch (r) {
            case ADMIN -> {
                list = service.findAll();                 // toutes les demandes
                req.setAttribute("scope", "all");
            }
            case PROPRIETAIRE -> {
                list = service.listForProprietaire(user.getId());  // uniquement ses immeubles
                req.setAttribute("scope", "owner");
            }
            case LOCATAIRE -> {
                list = service.findByLocataire(user.getId());      // mes demandes
                req.setAttribute("scope", "mine");
            }
            default -> list = List.of();
        }

        req.setAttribute("demandes", list);
        req.getRequestDispatcher("/WEB-INF/views/demande/list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        String action = trim(req.getParameter("action"));

        try {
            switch (action) {
                // Création par un locataire depuis le catalogue
                case "create" -> {
                    if (user.getRole() != Role.LOCATAIRE) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Réservé aux locataires");
                        return;
                    }
                    Long appartementId = parseId(req.getParameter("appartementId"));
                    String message = trim(req.getParameter("message"));
                    service.create(appartementId, user.getId(), message);
                    resp.sendRedirect(req.getContextPath() + "/app/demandes");
                }

                // Validation par ADMIN / PROPRIETAIRE
                case "approve" -> {
                    if (user.getRole() == Role.LOCATAIRE) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Réservé au propriétaire / admin");
                        return;
                    }
                    Long demandeId = parseId(req.getParameter("id"));
                    service.updateStatut(demandeId, DemandeStatut.ACCEPTEE);
                    resp.sendRedirect(req.getContextPath() + "/app/demandes");
                }

                // Rejet par ADMIN / PROPRIETAIRE
                case "reject" -> {
                    if (user.getRole() == Role.LOCATAIRE) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Réservé au propriétaire / admin");
                        return;
                    }
                    Long demandeId = parseId(req.getParameter("id"));
                    service.updateStatut(demandeId, DemandeStatut.REJETEE);
                    resp.sendRedirect(req.getContextPath() + "/app/demandes");
                }

                default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalStateException | IllegalArgumentException ex) {
            req.setAttribute("error", ex.getMessage());
            doGet(req, resp);
        }
    }

    // helpers
    private String trim(String s) { return s == null ? "" : s.trim(); }
    private Long parseId(String raw) { try { return raw == null ? null : Long.valueOf(raw.trim()); } catch (Exception e) { return null; } }
}
