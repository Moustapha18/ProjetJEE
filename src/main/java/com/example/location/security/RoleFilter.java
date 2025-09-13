package com.example.location.security;

import com.example.location.dto.UtilisateurDto;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class RoleFilter implements Filter {
    private EnumSet<Role> allowed = EnumSet.noneOf(Role.class);

    // actions qui écrivent (en plus de POST)
    private static final Set<String> WRITE_ACTIONS = Set.of(
            "new","edit","delete","create","update",
            "terminate","renew","gensched","markpaid"
    );

    @Override
    public void init(FilterConfig cfg) {
        String s = cfg.getInitParameter("allow");
        if (s != null) {
            for (String part : s.split(",")) {
                String p = part.trim().toUpperCase(Locale.ROOT);
                try { allowed.add(Role.valueOf(p)); } catch (IllegalArgumentException ignored) {}
            }
        }
        allowed.add(Role.ADMIN); // ADMIN toujours autorisé
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        boolean isWrite = "POST".equalsIgnoreCase(req.getMethod());
        if (!isWrite) {
            String action = Optional.ofNullable(req.getParameter("action"))
                    .orElse("").trim().toLowerCase(Locale.ROOT);
            isWrite = WRITE_ACTIONS.contains(action);
        }

        // lecture libre
        if (!isWrite) {
            chain.doFilter(request, response);
            return;
        }

        UtilisateurDto user = (UtilisateurDto) req.getSession().getAttribute("user");
        if (user == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Role role = user.getRole();
        if (!allowed.contains(role)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès réservé");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { /* no-op */ }
}
