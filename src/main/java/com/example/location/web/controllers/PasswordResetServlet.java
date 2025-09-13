package com.example.location.web.controllers;

import com.example.location.service.UtilisateurService;
import com.example.location.service.impl.UtilisateurServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(urlPatterns = {"/forgot", "/reset"})
public class PasswordResetServlet extends HttpServlet {

    private final UtilisateurService service = new UtilisateurServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/forgot".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot.jsp").forward(req, resp);
        } else if ("/reset".equals(path)) {
            String token = req.getParameter("token");
            if (token == null || token.isBlank()) { resp.sendError(400); return; }
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/forgot".equals(path)) {
            String email = req.getParameter("email");
            service.requestPasswordReset(email);
            req.setAttribute("info", "Si l’email existe, un lien de réinitialisation a été envoyé.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot.jsp").forward(req, resp);
        } else if ("/reset".equals(path)) {
            String token = req.getParameter("token");
            String pass1 = req.getParameter("password");
            String pass2 = req.getParameter("confirm");
            if (token == null || token.isBlank()) { resp.sendError(400); return; }
            if (pass1 == null || !pass1.equals(pass2)) {
                req.setAttribute("error", "Les mots de passe ne correspondent pas.");
                req.setAttribute("token", token);
                req.getRequestDispatcher("/WEB-INF/views/auth/reset.jsp").forward(req, resp);
                return;
            }
            boolean ok = service.resetPassword(token, pass1);
            if (ok) {
                req.setAttribute("info", "Mot de passe réinitialisé. Vous pouvez vous connecter.");
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Lien invalide ou expiré.");
                req.setAttribute("token", token);
                req.getRequestDispatcher("/WEB-INF/views/auth/reset.jsp").forward(req, resp);
            }
        }
    }
}
