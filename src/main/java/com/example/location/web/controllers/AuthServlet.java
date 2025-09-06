package com.example.location.web.controllers;

import com.example.location.dto.UtilisateurDto;
import com.example.location.security.Role;
import com.example.location.service.UtilisateurService;
import com.example.location.service.impl.UtilisateurServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet(urlPatterns = {"/login", "/logout", "/register"})
public class AuthServlet extends HttpServlet {

    private UtilisateurService users;

    @Override public void init() { this.users = new UtilisateurServiceImpl(); }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if ("/register".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/register".equals(path)) {
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            users.register(username, email, password, Role.USER);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        Optional<UtilisateurDto> ok = users.authenticate(username, password);
        if (ok.isPresent()) {
            req.getSession().setAttribute("user", ok.get());
            resp.sendRedirect(req.getContextPath() + "/app/immeubles");
        } else {
            req.setAttribute("error", "Identifiants invalides");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }
}
