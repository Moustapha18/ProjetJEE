// src/main/java/com/example/location/web/controllers/ImmeubleServlet.java
package com.example.location.web.controllers;

import com.example.location.entity.Immeuble;
import com.example.location.service.ImmeubleService;
import com.example.location.service.impl.ImmeubleServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "ImmeubleServlet", urlPatterns = {"/app/immeubles", "/app/immeuble"})
public class ImmeubleServlet extends HttpServlet {

    private final ImmeubleService service = new ImmeubleServiceImpl();

    private Integer li(String s){ try { return (s==null||s.isBlank())? null : Integer.valueOf(s.trim()); } catch(Exception e){ return null; } }
    private BigDecimal bd(String s){ try { return (s==null||s.isBlank())? null : new BigDecimal(s.trim()); } catch(Exception e){ return null; } }
    private String trim(String s) { return s == null ? "" : s.trim(); }
    private Long parseId(String raw) { try { return Long.valueOf(raw); } catch(Exception e){ throw new IllegalArgumentException("id invalide"); } }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = trim(req.getParameter("action"));

        if (action.isEmpty() || action.equals("list")) {
            req.setAttribute("immeubles", service.findAll());
            req.getRequestDispatcher("/WEB-INF/views/immeuble/list.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "new" -> {
                req.setAttribute("mode", "create");
                req.getRequestDispatcher("/WEB-INF/views/immeuble/form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Long id = parseId(req.getParameter("id"));
                service.findById(id).ifPresent(i -> req.setAttribute("immeuble", i));
                req.setAttribute("mode", "edit");
                req.getRequestDispatcher("/WEB-INF/views/immeuble/form.jsp").forward(req, resp);
            }
            case "delete" -> {
                Long id = parseId(req.getParameter("id"));
                service.deleteById(id);
                resp.sendRedirect(req.getContextPath() + "/app/immeubles");
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = trim(req.getParameter("action"));

        String nom           = trim(req.getParameter("nom"));
        String adresse       = trim(req.getParameter("adresse"));
        String ville         = trim(req.getParameter("ville"));
        String codePostal    = trim(req.getParameter("codePostal"));
        String description   = trim(req.getParameter("description"));
        Integer anneeConstr  = li(req.getParameter("anneeConstruction"));
        Integer nbEtages     = li(req.getParameter("nbEtages"));
        Integer nbAppts      = li(req.getParameter("nbAppartements"));
        BigDecimal surface   = bd(req.getParameter("surfaceTotale"));

        if (nom.isEmpty() || adresse.isEmpty() || ville.isEmpty()) {
            req.setAttribute("error", "Nom, adresse et ville sont obligatoires.");
            req.setAttribute("mode", action.equals("update") ? "edit" : "create");
            if (action.equals("update")) {
                Long id = parseId(req.getParameter("id"));
                service.findById(id).ifPresent(i -> req.setAttribute("immeuble", i));
            }
            req.getRequestDispatcher("/WEB-INF/views/immeuble/form.jsp").forward(req, resp);
            return;
        }

        Immeuble data = new Immeuble(
                null, nom, adresse, ville, codePostal, description,
                anneeConstr, nbEtages, nbAppts, surface
        );

        if (action.equals("update")) {
            Long id = parseId(req.getParameter("id"));
            service.update(id, data);
        } else {
            service.save(data);
        }
        resp.sendRedirect(req.getContextPath() + "/app/immeubles");
    }
}
