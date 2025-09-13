package com.example.location.web.controllers;

import com.example.location.security.Role;
import com.example.location.service.UtilisateurService;
import com.example.location.service.impl.UtilisateurServiceImpl;
import com.example.location.dto.UtilisateurDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Optional;

import static com.example.location.security.Role.*;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/logout", "/register"})
public class AuthServlet extends HttpServlet {

    private final UtilisateurService utilisateurService = new UtilisateurServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("/login".equals(path)) {
            if ("1".equals(req.getParameter("registered"))) {
                req.setAttribute("info", "Compte créé avec succès. Veuillez vous connecter.");
            }
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        if ("/register".equals(path)) {
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Optional<UtilisateurDto> userOpt = utilisateurService.authenticate(username, password);

        if (userOpt.isPresent()) {
            UtilisateurDto user = userOpt.get();
            req.getSession().setAttribute("user", user);

            // 🔥 Redirection vers l'accueil après connexion
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
        } else {
            req.setAttribute("error", "Identifiants invalides.");
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }


    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String roleParam = req.getParameter("role");

        // On limite aux rôles visibles côté UI
        Role role = "PROPRIETAIRE".equals(roleParam) ? Role.PROPRIETAIRE : Role.LOCATAIRE;

        try {
            // 1) Création côté service (avec validations)
            utilisateurService.register(username, email, password, role);

            // 2) Surtout NE PAS se connecter automatiquement
            //    On redirige vers /login avec un flag de succès
            resp.sendRedirect(req.getContextPath() + "/login?registered=1");
        } catch (IllegalArgumentException ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Erreur lors de l’inscription.");
            req.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(req, resp);
        }
    }


}
