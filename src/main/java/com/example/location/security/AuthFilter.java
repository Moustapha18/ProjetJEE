package com.example.location.security;

import com.example.location.dto.UtilisateurDto;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class AuthFilter implements Filter {
    private boolean securityEnabled = true;

    @Override
    public void init(FilterConfig filterConfig) {
        String p = filterConfig.getServletContext().getInitParameter("app.security.enabled");
        if (p != null && p.equalsIgnoreCase("false")) {
            securityEnabled = false; // DEV: bypass total
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!securityEnabled) {
            // Mode DEV: pas de contrôle, on laisse passer
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        UtilisateurDto u = (UtilisateurDto) req.getSession().getAttribute("user");
        if (u == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        chain.doFilter(request, response);
    }

    @Override public void destroy() {}
}
