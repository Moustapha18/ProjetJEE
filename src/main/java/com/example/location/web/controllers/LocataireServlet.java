package com.example.location.web.controllers;

import com.example.location.entity.Locataire;
import com.example.location.service.LocataireService;
import com.example.location.service.impl.LocataireServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "LocataireServlet", urlPatterns = {"/app/locataires"})
public class LocataireServlet extends HttpServlet {

    private final LocataireService service = new LocataireServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = trim(req.getParameter("action"));

        if (action.isEmpty() || action.equals("list")) {
            req.setAttribute("locataires", service.findAll());
            req.getRequestDispatcher("/WEB-INF/views/locataire/list.jsp").forward(req, resp);
            return;
        }

        switch (action) {
            case "new" -> {
                req.setAttribute("mode", "create");
                req.getRequestDispatcher("/WEB-INF/views/locataire/form.jsp").forward(req, resp);
            }
            case "edit" -> {
                Long id = parseId(req.getParameter("id"));
                service.findById(id).ifPresent(l -> req.setAttribute("locataire", l));
                req.setAttribute("mode", "edit");
                req.getRequestDispatcher("/WEB-INF/views/locataire/form.jsp").forward(req, resp);
            }
            case "delete" -> {
                Long id = parseId(req.getParameter("id"));
                service.deleteById(id);
                resp.sendRedirect(req.getContextPath() + "/app/locataires");
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = trim(req.getParameter("action"));

        String nomComplet = trim(req.getParameter("nomComplet"));
        String email = trim(req.getParameter("email"));
        String telephone = trim(req.getParameter("telephone"));

        if (nomComplet.isEmpty()) {
            req.setAttribute("error", "Le nom complet est obligatoire.");
            req.setAttribute("mode", action.equals("update") ? "edit" : "create");
            if (action.equals("update")) {
                Long id = parseId(req.getParameter("id"));
                service.findById(id).ifPresent(l -> req.setAttribute("locataire", l));
            }
            req.getRequestDispatcher("/WEB-INF/views/locataire/form.jsp").forward(req, resp);
            return;
        }

        if (action.equals("update")) {
            Long id = parseId(req.getParameter("id"));
            service.update(id, new Locataire(null, nomComplet, email, telephone));
        } else {
            service.save(new Locataire(null, nomComplet, email, telephone));
        }
        resp.sendRedirect(req.getContextPath() + "/app/locataires");
    }

    private Long parseId(String raw) {
        try { return Long.valueOf(raw); } catch (Exception e) { throw new IllegalArgumentException("id invalide"); }
    }
    private String trim(String s) { return s == null ? "" : s.trim(); }
}
