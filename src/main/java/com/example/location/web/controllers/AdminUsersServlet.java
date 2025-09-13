package com.example.location.web.controllers;

import com.example.location.dto.UtilisateurDto;
import com.example.location.entity.Utilisateur;
import com.example.location.security.Role;
import com.example.location.service.UtilisateurService;
import com.example.location.service.impl.UtilisateurServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/app/admin/users"})
public class AdminUsersServlet extends HttpServlet {

    private final UtilisateurService service = new UtilisateurServiceImpl();

    private boolean isAdmin(HttpServletRequest req) {
        UtilisateurDto u = (UtilisateurDto) req.getSession().getAttribute("user");
        return u != null && u.getRole() == Role.ADMIN;
    }

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) { resp.sendError(403); return; }

        String action = param(req, "action");
        if ("new".equals(action)) {
            req.getRequestDispatcher("/WEB-INF/views/admin/users/form.jsp").forward(req, resp);
            return;
        }
        if ("edit".equals(action)) {
            Long id = parseId(req.getParameter("id"));
            Optional<Utilisateur> u = service.adminFindById(id);
            if (u.isEmpty()) { resp.sendError(404); return; }
            req.setAttribute("userEdit", u.get());
            req.getRequestDispatcher("/WEB-INF/views/admin/users/form.jsp").forward(req, resp);
            return;
        }

        // default: list
        List<Utilisateur> list = service.adminFindAll();
        req.setAttribute("users", list);
        req.getRequestDispatcher("/WEB-INF/views/admin/users/list.jsp").forward(req, resp);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) { resp.sendError(403); return; }

        String action = param(req, "action");
        try {
            switch (action) {
                case "create" -> {
                    String username = param(req, "username");
                    String email = param(req, "email");
                    String password = param(req, "password");
                    Role role = Role.valueOf(param(req, "role"));
                    service.adminCreate(username, email, password, role);
                    resp.sendRedirect(req.getContextPath() + "/app/admin/users");
                }
                case "updateRole" -> {
                    Long id = parseId(req.getParameter("id"));
                    Role role = Role.valueOf(param(req, "role"));
                    service.adminUpdateRole(id, role);
                    resp.sendRedirect(req.getContextPath() + "/app/admin/users");
                }
                case "resetPassword" -> {
                    Long id = parseId(req.getParameter("id"));
                    String newPass = param(req, "newPassword");
                    service.adminResetPassword(id, newPass);
                    resp.sendRedirect(req.getContextPath() + "/app/admin/users");
                }
                case "delete" -> {
                    Long id = parseId(req.getParameter("id"));
                    service.adminDelete(id);
                    resp.sendRedirect(req.getContextPath() + "/app/admin/users");
                }
                default -> resp.sendError(400);
            }

        } catch (IllegalStateException ex) {
            req.setAttribute("error", ex.getMessage());
            doGet(req, resp);
        } catch (jakarta.persistence.RollbackException ex) {
            // Filet de sécurité si la contrainte DB remonte quand même
            req.setAttribute("error", "Suppression impossible : l’utilisateur est encore référencé (immeubles/contrats...).");
            doGet(req, resp);
        }

    }

    private String param(HttpServletRequest req, String n) { String v=req.getParameter(n); return v==null?"":v.trim(); }
    private Long parseId(String raw) { try { return raw==null?null:Long.valueOf(raw.trim()); } catch(Exception e){ return null; } }
}
