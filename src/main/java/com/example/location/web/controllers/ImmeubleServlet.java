package com.example.location.web.controllers;

import com.example.location.config.JpaUtil;
import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Immeuble;
import com.example.location.entity.Utilisateur;
import com.example.location.security.Role;
import com.example.location.service.ImmeubleService;
import com.example.location.service.impl.ImmeubleServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(urlPatterns = {"/app/immeubles"})
public class ImmeubleServlet extends HttpServlet {

    private ImmeubleService service;

    @Override
    public void init() {
        service = new ImmeubleServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        String action = trim(req.getParameter("action"));

        if (action.isEmpty() || action.equals("list")) {
            var list = (user != null && user.getRole() == Role.PROPRIETAIRE)
                    ? service.findByProprietaireId(user.getId())
                    : service.findAll();
            req.setAttribute("immeubles", list);
            req.getRequestDispatcher("/WEB-INF/views/immeuble/list.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "new" -> {
                req.setAttribute("mode", "create");
                // ADMIN : liste des propriétaires
                if (user != null && user.getRole() == Role.ADMIN) {
                    req.setAttribute("owners", findAllOwners());
                }
                req.getRequestDispatcher("/WEB-INF/views/immeuble/form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Long id = parseId(req.getParameter("id"));
                Immeuble im = service.findById(id).orElse(null);
                if (im == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                if (!canWrite(user, im)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }

                req.setAttribute("mode", "edit");
                req.setAttribute("immeuble", im);
                if (user != null && user.getRole() == Role.ADMIN) {
                    req.setAttribute("owners", findAllOwners());
                }
                req.getRequestDispatcher("/WEB-INF/views/immeuble/form.jsp").forward(req, resp);
            }
            case "delete" -> {
                Long id = parseId(req.getParameter("id"));
                Immeuble im = service.findById(id).orElse(null);
                if (im == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                if (!canWrite(user, im)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }

                service.deleteById(id);
                resp.sendRedirect(req.getContextPath() + "/app/immeubles");
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        String action = trim(req.getParameter("action"));

        Immeuble data = new Immeuble();
        data.setNom(trim(req.getParameter("nom")));
        data.setAdresse(trim(req.getParameter("adresse")));
        data.setVille(trim(req.getParameter("ville")));
        data.setCodePostal(trim(req.getParameter("codePostal")));
        data.setDescription(trim(req.getParameter("description")));
        data.setAnneeConstruction(parseIntObj(req.getParameter("anneeConstruction")));
        data.setNbEtages(parseIntObj(req.getParameter("nbEtages")));
        data.setNbAppartements(parseIntObj(req.getParameter("nbAppartements")));
        data.setSurfaceTotale(BigDecimal.valueOf(parseDouble(req.getParameter("surfaceTotale"))));

        // ASSIGNATION PROPRIETAIRE
        if (user != null && user.getRole() == Role.PROPRIETAIRE) {
            // forcer le propriétaire courant
            Utilisateur owner = refUtilisateur(user.getId());
            data.setProprietaire(owner);
        } else if (user != null && user.getRole() == Role.ADMIN) {
            Long propId = parseId(req.getParameter("proprietaireId"));
            if (propId != null) {
                data.setProprietaire(refUtilisateur(propId));
            } else {
                data.setProprietaire(null);
            }
        }

        try {
            switch (action) {
                case "create" -> {
                    service.save(data);
                }
                case "update" -> {
                    Long id = parseId(req.getParameter("id"));
                    Immeuble ex = service.findById(id).orElse(null);
                    if (ex == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                    if (!canWrite(user, ex)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }
                    service.update(id, data);
                }
                default -> { /* no-op */ }
            }
            resp.sendRedirect(req.getContextPath() + "/app/immeubles");
        } catch (IllegalArgumentException ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("immeuble", data);
            req.setAttribute("mode", "update".equals(action) ? "edit" : "create");
            if (user != null && user.getRole() == Role.ADMIN) {
                req.setAttribute("owners", findAllOwners());
            }
            req.getRequestDispatcher("/WEB-INF/views/immeuble/form.jsp").forward(req, resp);
        }
    }

    // --------- helpers

    private boolean canWrite(UtilisateurDto user, Immeuble im) {
        if (user == null) return false;
        if (user.getRole() == Role.ADMIN) return true;
        return user.getRole() == Role.PROPRIETAIRE
                && im.getProprietaire() != null
                && user.getId().equals(im.getProprietaire().getId());
    }

    private List<Utilisateur> findAllOwners() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("""
                    select u from Utilisateur u
                    where u.role = com.example.location.security.Role.PROPRIETAIRE
                    order by u.username asc
                """, Utilisateur.class).getResultList();
        } finally { em.close(); }
    }

    private Utilisateur refUtilisateur(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.getReference(Utilisateur.class, id);
        } finally { em.close(); }
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
    private Long parseId(String raw){ try { return raw==null?null:Long.valueOf(raw.trim()); } catch(Exception e){ return null; } }
    private Integer parseIntObj(String raw){ try { return raw==null||raw.isBlank()?null:Integer.valueOf(raw.trim()); } catch(Exception e){ return null; } }
    private Double parseDouble(String raw){ try { return raw==null||raw.isBlank()?null:Double.valueOf(raw.trim()); } catch(Exception e){ return null; } }
}
