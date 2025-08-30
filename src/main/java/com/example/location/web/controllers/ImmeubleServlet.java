package com.example.location.web.controllers;

import com.example.location.dto.ImmeubleDto;
import com.example.location.service.ImmeubleService;
import com.example.location.service.impl.ImmeubleServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SRP: gestion des flux HTTP pour Immeuble.
 * Controller mince, délègue au service (Respecte DIP).
 */
@WebServlet(urlPatterns = {"/app/immeubles", "/app/immeubles/new"})
public class ImmeubleServlet extends HttpServlet {

    private ImmeubleService service;

    @Override public void init() {
        this.service = new ImmeubleServiceImpl();
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/app/immeubles/new".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/immeubles/form.jsp").forward(req, resp);
        } else {
            req.setAttribute("items", service.lister());
            req.getRequestDispatcher("/WEB-INF/views/immeubles/list.jsp").forward(req, resp);
        }
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nom = req.getParameter("nom");
        String adresse = req.getParameter("adresse");
        service.creer(new ImmeubleDto(null, nom, adresse));
        resp.sendRedirect(req.getContextPath() + "/app/immeubles");
    }
}
