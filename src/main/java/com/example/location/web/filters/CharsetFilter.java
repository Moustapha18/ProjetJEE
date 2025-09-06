package com.example.location.web.filters;

import jakarta.servlet.*;
import java.io.IOException;

/** Force UTF-8. */
public class CharsetFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
    }
}
