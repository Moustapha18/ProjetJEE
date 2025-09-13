package com.example.location.web.controllers;

import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Appartement;
import com.example.location.entity.Immeuble;
import com.example.location.security.Role;
import com.example.location.service.AppartementService;
import com.example.location.service.ImmeubleService;
import com.example.location.service.impl.AppartementServiceImpl;
import com.example.location.service.impl.ImmeubleServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "AppartementServlet", urlPatterns = {"/app/appartements"})
public class AppartementServlet extends HttpServlet {

    private AppartementService service;
    private ImmeubleService immeubleService;

    @Override
    public void init() {
        this.service = new AppartementServiceImpl();
        this.immeubleService = new ImmeubleServiceImpl();
    }

    /** Génère le HTML des <option> pour le select Immeuble (sans JSTL) */
    private String buildImmeubleOptions(List<Immeuble> ims, Long selectedId) {
        if (ims == null || ims.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Immeuble im : ims) {
            Long id = im.getId();
            String nom = (im.getNom() != null && !im.getNom().isBlank()) ? im.getNom() : ("Immeuble #" + id);
            String ville = (im.getVille() != null && !im.getVille().isBlank()) ? (" — " + im.getVille()) : "";
            sb.append("<option value='").append(id).append("'");
            if (selectedId != null && selectedId.equals(id)) sb.append(" selected");
            sb.append(">").append(nom).append(ville).append("</option>");
        }
        return sb.toString();
    }

    /** Badge “Disponibilité” à partir de occupe/reserve */
    private String buildDispoBadge(Appartement a) {
        if (a.isOccupe()) {
            return "<span class='badge badge-occupe'>Occupé</span>";
        }
        if (a.isReserve()) {
            return "<span class='badge bg-warning text-dark'>Réservé</span>";
        }
        return "<span class='badge badge-dispo'>Disponible</span>";
    }

    /** Construit les lignes HTML du tableau (sans JSTL) */
    private String buildTableAppartements(List<Appartement> list, HttpServletRequest req, UtilisateurDto user) {
        if (list == null || list.isEmpty()) {
            return "<tr><td colspan='10' class='text-center text-muted py-4'>Aucun appartement trouvé.</td></tr>";
        }
        String ctx = req.getContextPath();
        NumberFormat nf = NumberFormat.getInstance(new Locale("fr", "SN"));

        StringBuilder sb = new StringBuilder();
        for (Appartement a : list) {
            String immeubleNom = a.getImmeuble() != null && a.getImmeuble().getNom() != null
                    ? a.getImmeuble().getNom() : "";
            String ville = a.getImmeuble() != null && a.getImmeuble().getVille() != null
                    ? a.getImmeuble().getVille() : "";
            String loyerStr = (a.getLoyer() != null) ? nf.format(a.getLoyer()) : "";

            // Actions (edit/delete pour ADMIN; PROPRIETAIRE seulement si c’est son immeuble)
            boolean canEditDelete = false;
            if (user != null) {
                if (user.getRole() == Role.ADMIN) {
                    canEditDelete = true;
                } else if (user.getRole() == Role.PROPRIETAIRE) {
                    Immeuble im = a.getImmeuble();
                    canEditDelete = (im != null && im.getProprietaire() != null
                            && user.getId().equals(im.getProprietaire().getId()));
                }
            }

            String actions = "";
            if (canEditDelete) {
                actions =
                        "<a href='" + ctx + "/app/appartements?action=edit&id=" + a.getId() + "' class='btn btn-sm btn-outline-primary me-1'>"
                                + "<i class='bi bi-pencil'></i></a>"
                                + "<a href='" + ctx + "/app/appartements?action=delete&id=" + a.getId() + "' "
                                + "class='btn btn-sm btn-outline-danger' "
                                + "onclick=\"return confirm('Supprimer cet appartement ?');\">"
                                + "<i class='bi bi-trash'></i></a>";
            }

            // ===== AJOUT : bouton Réserver pour le LOCATAIRE quand dispo =====
            boolean isLocataire = (user != null && user.getRole() == Role.LOCATAIRE);
            boolean isDisponible = !a.isOccupe() && !a.isReserve();
            if (isLocataire && isDisponible) {
                actions +=
                        (actions.isEmpty() ? "" : " ")
                                + "<form method='post' action='" + ctx + "/app/demandes' style='display:inline-block;margin-left:.25rem'>"
                                + "<input type='hidden' name='action' value='create'/>"
                                + "<input type='hidden' name='appartementId' value='" + a.getId() + "'/>"
                                + "<button type='submit' class='btn btn-sm btn-outline-success'>"
                                + "<i class='bi bi-bookmark-plus'></i> Réserver</button>"
                                + "</form>";
            }
            // =================================================================

            sb.append("<tr>")
                    .append("<td>").append(a.getId() != null ? a.getId() : "").append("</td>")
                    .append("<td>").append(a.getNumero() != null ? a.getNumero() : "").append("</td>")
                    .append("<td>").append(a.getEtage() != null ? a.getEtage() : "").append("</td>")
                    .append("<td>").append(a.getSurface() != null ? a.getSurface() : "").append("</td>")
                    .append("<td>").append(a.getNbPieces() != null ? a.getNbPieces() : "").append("</td>")
                    .append("<td>").append(loyerStr).append("</td>")
                    .append("<td>").append(immeubleNom).append("</td>")
                    .append("<td>").append(ville).append("</td>")
                    .append("<td>").append(buildDispoBadge(a)).append("</td>")
                    .append("<td class='text-end'>").append(actions).append("</td>")
                    .append("</tr>");
        }
        return sb.toString();
    }

    private boolean canWrite(UtilisateurDto user, Appartement a) {
        if (user == null) return false;
        if (user.getRole() == Role.ADMIN) return true;
        if (user.getRole() == Role.PROPRIETAIRE) {
            Immeuble im = a.getImmeuble();
            return im != null && im.getProprietaire() != null
                    && user.getId().equals(im.getProprietaire().getId());
        }
        return false;
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
    private String trimOrNull(String s) { if (s == null) return null; String t = s.trim(); return t.isEmpty() ? null : t; }
    private Long parseId(String raw) { try { return raw == null ? null : Long.valueOf(raw.trim()); } catch (Exception e) { return null; } }
    private Integer parseIntObj(String raw) { try { return raw == null || raw.isBlank() ? null : Integer.valueOf(raw.trim()); } catch (Exception e) { return null; } }
    private Double parseDouble(String raw) { try { return raw == null || raw.isBlank() ? null : Double.valueOf(raw.trim()); } catch (Exception e) { return null; } }
    private BigDecimal parseDecimal(String raw) { try { return raw == null || raw.isBlank() ? null : new BigDecimal(raw.trim()); } catch (Exception e) { return null; } }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        String action = trim(req.getParameter("action"));

        boolean isAdmin        = user != null && user.getRole() == Role.ADMIN;
        boolean isProprietaire = user != null && user.getRole() == Role.PROPRIETAIRE;
        boolean canCreate      = isAdmin || isProprietaire;

        req.setAttribute("canCreate", canCreate);

        String ctx = req.getContextPath();
        String btnHtml = canCreate
                ? ("<a href='" + ctx + "/app/appartements?action=new' class='btn btn-success mb-3'>"
                + "<i class=\"bi bi-plus-lg\"></i> Nouvel appartement"
                + "</a>")
                : "";
        req.setAttribute("btnNouveauAppartement", btnHtml);

        if (action.isEmpty()) {
            List<Appartement> list = service.searchFiltered(
                    parseDecimal(req.getParameter("min")),
                    parseDecimal(req.getParameter("max")),
                    trimOrNull(req.getParameter("ville")),
                    parseIntObj(req.getParameter("pieces"))
            );
            req.setAttribute("tableAppartements", buildTableAppartements(list, req, user));
            req.getRequestDispatcher("/WEB-INF/views/appartement/list.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "list" -> {
                String immeubleId = req.getParameter("immeubleId");
                List<Appartement> list = (immeubleId != null && !immeubleId.isBlank())
                        ? service.findByImmeubleId(Long.valueOf(immeubleId))
                        : service.findAll();
                req.setAttribute("tableAppartements", buildTableAppartements(list, req, user));
                req.getRequestDispatcher("/WEB-INF/views/appartement/list.jsp").forward(req, resp);
            }

            case "new" -> {
                if (!canCreate) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }

                List<Immeuble> ims;
                if (isProprietaire) {
                    ims = immeubleService.findByProprietaireId(user.getId());
                    if (ims == null || ims.isEmpty()) {
                        ims = immeubleService.findAll();
                    }
                } else {
                    ims = immeubleService.findAll();
                }

                req.setAttribute("pageTitle", "Nouvel appartement");
                req.setAttribute("formTitle", "Créer un appartement");
                req.setAttribute("hiddenFields",
                        "<input type='hidden' name='action' value='create'/>");
                req.setAttribute("optionsImmeubles", buildImmeubleOptions(ims, null));
                req.setAttribute("numero", "");
                req.setAttribute("etage", "");
                req.setAttribute("surface", "");
                req.setAttribute("nbPieces", "");
                req.setAttribute("loyer", "");

                req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            }

            case "edit" -> {
                Long id = parseId(req.getParameter("id"));
                Appartement a = service.findById(id).orElse(null);
                if (a == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                if (!canWrite(user, a)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }

                List<Immeuble> ims;
                if (isProprietaire) {
                    ims = immeubleService.findByProprietaireId(user.getId());
                    if (ims == null || ims.isEmpty()) ims = immeubleService.findAll();
                } else {
                    ims = immeubleService.findAll();
                }
                Long selectedImId = (a.getImmeuble() != null) ? a.getImmeuble().getId() : null;

                req.setAttribute("pageTitle", "Modifier appartement");
                req.setAttribute("formTitle", "Modifier l'appartement");
                req.setAttribute("hiddenFields",
                        "<input type='hidden' name='action' value='update'/>" +
                                "<input type='hidden' name='id' value='" + a.getId() + "'/>");
                req.setAttribute("optionsImmeubles", buildImmeubleOptions(ims, selectedImId));
                req.setAttribute("numero", a.getNumero());
                req.setAttribute("etage", a.getEtage());
                req.setAttribute("surface", a.getSurface());
                req.setAttribute("nbPieces", a.getNbPieces());
                req.setAttribute("loyer", a.getLoyer());

                req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            }

            case "delete" -> {
                Long id = parseId(req.getParameter("id"));
                Appartement a = service.findById(id).orElse(null);
                if (a == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
                if (!canWrite(user, a)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }

                service.deleteById(id);
                resp.sendRedirect(req.getContextPath() + "/app/appartements");
            }

            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        String action = trim(req.getParameter("action"));

        String numero = trim(req.getParameter("numero"));
        Integer etage = parseIntObj(req.getParameter("etage"));
        Double surface = parseDouble(req.getParameter("surface"));
        BigDecimal loyer = parseDecimal(req.getParameter("loyer"));
        Integer nbPieces = parseIntObj(req.getParameter("nbPieces"));
        Long immeubleId = parseId(req.getParameter("immeubleId"));

        boolean isProprietaire = user != null && user.getRole() == Role.PROPRIETAIRE;

        if (numero.isEmpty() || immeubleId == null) {
            req.setAttribute("errorMsg", "<div class='alert alert-danger'>Numéro et Immeuble sont obligatoires.</div>");

            boolean isEdit = "update".equals(action);
            req.setAttribute("pageTitle", isEdit ? "Modifier appartement" : "Nouvel appartement");
            req.setAttribute("formTitle", isEdit ? "Modifier l'appartement" : "Créer un appartement");
            req.setAttribute("hiddenFields", isEdit
                    ? ("<input type='hidden' name='action' value='update'/>"
                    + "<input type='hidden' name='id' value='" + req.getParameter("id") + "'/>")
                    : "<input type='hidden' name='action' value='create'/>");

            List<Immeuble> ims = isProprietaire
                    ? immeubleService.findByProprietaireId(user.getId())
                    : immeubleService.findAll();
            if (ims == null || ims.isEmpty()) ims = immeubleService.findAll();
            req.setAttribute("optionsImmeubles", buildImmeubleOptions(ims, immeubleId));

            req.setAttribute("numero", numero);
            req.setAttribute("etage", etage);
            req.setAttribute("surface", surface);
            req.setAttribute("nbPieces", nbPieces);
            req.setAttribute("loyer", loyer);

            req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            return;
        }

        Immeuble im = immeubleService.findById(immeubleId).orElse(null);
        if (im == null) {
            req.setAttribute("errorMsg", "<div class='alert alert-danger'>Immeuble introuvable.</div>");
            req.setAttribute("pageTitle", "Nouvel appartement");
            req.setAttribute("formTitle", "Créer un appartement");
            req.setAttribute("hiddenFields", "<input type='hidden' name='action' value='create'/>");

            List<Immeuble> ims = isProprietaire
                    ? immeubleService.findByProprietaireId(user.getId())
                    : immeubleService.findAll();
            if (ims == null || ims.isEmpty()) ims = immeubleService.findAll();
            req.setAttribute("optionsImmeubles", buildImmeubleOptions(ims, null));

            req.setAttribute("numero", numero);
            req.setAttribute("etage", etage);
            req.setAttribute("surface", surface);
            req.setAttribute("nbPieces", nbPieces);
            req.setAttribute("loyer", loyer);

            req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            return;
        }

        if (isProprietaire) {
            if (im.getProprietaire() == null || !im.getProprietaire().getId().equals(user.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous ne pouvez agir que sur vos immeubles.");
                return;
            }
        }

        Appartement a = new Appartement();
        a.setNumero(numero);
        a.setEtage(etage);
        a.setSurface(surface);
        a.setLoyer(loyer);
        a.setNbPieces(nbPieces);
        a.setImmeuble(im);

        if ("update".equals(action)) {
            Long id = parseId(req.getParameter("id"));
            Appartement ex = service.findById(id).orElse(null);
            if (ex == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
            if (!canWrite(user, ex)) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }

            service.update(id, a);
        } else {
            service.save(a);
        }

        resp.sendRedirect(req.getContextPath() + "/app/appartements");
    }
}
