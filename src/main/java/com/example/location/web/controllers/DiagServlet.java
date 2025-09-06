package com.example.location.web.controllers;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;

@WebServlet(name="DiagServlet", urlPatterns={"/diag"})
public class DiagServlet extends HttpServlet {
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain; charset=UTF-8");

        String dir = "/WEB-INF/views/";
        Set<String> paths = getServletContext().getResourcePaths(dir);

        resp.getWriter().println("ContextPath: " + req.getContextPath());
        resp.getWriter().println("Listing: " + dir);
        if (paths == null || paths.isEmpty()) {
            resp.getWriter().println("=> VIDE (Tomcat ne voit rien dans " + dir + ")");
        } else {
            for (String p : paths) resp.getWriter().println(" - " + p);
        }

        String file = "/WEB-INF/views/immeuble/list.jsp";
        boolean exists = getServletContext().getResourceAsStream(file) != null;
        resp.getWriter().println("\nCheck file: " + file + " -> " + exists);
    }
}
