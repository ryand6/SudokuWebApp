package com.github.ryand6.sudokuweb.controllers.testing;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class BetaAccessFilter extends OncePerRequestFilter {

    private static final String SESSION_KEY = "betaAccessGranted";

    private final boolean enabled;

    public BetaAccessFilter(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return !enabled
                || "OPTIONS".equalsIgnoreCase(request.getMethod())
                || "/beta-access".equals(path)
                || "/actuator/health".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        var session = request.getSession(false);

        if (session == null ||
                !Boolean.TRUE.equals(session.getAttribute(SESSION_KEY))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"BETA_ACCESS_REQUIRED\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}

