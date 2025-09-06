package com.example.location.web.controllers;

import com.example.location.entity.Appartement;
import com.example.location.entity.Immeuble;
import com.example.location.service.AppartementService;
import com.example.location.service.ImmeubleService;
import com.example.location.service.impl.AppartementServiceImpl;
import com.example.location.service.impl.ImmeubleServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "AppartementServlet", urlPatterns = {"/app/appartements"})
public class AppartementServlet extends HttpServlet {

    private final AppartementService service = new AppartementServiceImpl();
    private final ImmeubleService immeubleService = new ImmeubleServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = trim(req.getParameter("action"));

        if (action.isEmpty() || action.equals("list")) {
            String immeubleId = req.getParameter("immeubleId");
            if (immeubleId != null && !immeubleId.isBlank()) {
                req.setAttribute("appartements", service.findByImmeubleId(Long.valueOf(immeubleId)));
            } else {
                req.setAttribute("appartements", service.findAll());
            }
            req.getRequestDispatcher("/WEB-INF/views/appartement/list.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "new" -> {
                req.setAttribute("immeubles", immeubleService.findAll());
                req.setAttribute("mode", "create");
                req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Long id = parseId(req.getParameter("id"));
                service.findById(id).ifPresent(a -> req.setAttribute("appartement", a));
                req.setAttribute("immeubles", immeubleService.findAll());
                req.setAttribute("mode", "edit");
                req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            }
            case "delete" -> {
                Long id = parseId(req.getParameter("id"));
                service.deleteById(id);
                resp.sendRedirect(req.getContextPath() + "/app/appartements");
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = trim(req.getParameter("action"));

        String numero = trim(req.getParameter("numero"));
        Integer etage = parseInt(req.getParameter("etage"));
        Double surface = parseDouble(req.getParameter("surface"));
        Double loyer = parseDouble(req.getParameter("loyer"));
        Long immeubleId = parseId(req.getParameter("immeubleId"));

        if (numero.isEmpty() || immeubleId == null) {
            req.setAttribute("error", "Numéro et Immeuble sont obligatoires.");
            req.setAttribute("mode", action.equals("update") ? "edit" : "create");
            req.setAttribute("immeubles", immeubleService.findAll());
            if (action.equals("update")) {
                Long id = parseId(req.getParameter("id"));
                service.findById(id).ifPresent(a -> req.setAttribute("appartement", a));
            }
            req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            return;
        }

        Immeuble im = immeubleService.findById(immeubleId).orElse(null);
        if (im == null) {
            req.setAttribute("error", "Immeuble introuvable.");
            req.setAttribute("mode", action.equals("update") ? "edit" : "create");
            req.setAttribute("immeubles", immeubleService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/appartement/form.jsp").forward(req, resp);
            return;
        }

        Appartement a = new Appartement(null, numero, etage, surface, loyer, im);

        if (action.equals("update")) {
            Long id = parseId(req.getParameter("id"));
            service.update(id, a);
        } else {
            service.save(a);
        }
        resp.sendRedirect(req.getContextPath() + "/app/appartements");
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
    private Long parseId(String raw) { try { return raw == null ? null : Long.valueOf(raw.trim()); } catch (Exception e) { return null; } }
    private Integer parseInt(String raw) { try { return raw == null || raw.isBlank() ? null : Integer.valueOf(raw.trim()); } catch (Exception e) { return null; } }
    private Double parseDouble(String raw) { try { return raw == null || raw.isBlank() ? null : Double.valueOf(raw.trim()); } catch (Exception e) { return null; } }
}
