package com.github.ryand6.sudokuweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final String spaBaseUrl;
    private final CookieCsrfTokenRepository csrfTokenRepository;

    public OAuth2SuccessHandler(String spaBaseUrl,
                                CookieCsrfTokenRepository csrfTokenRepository) {
        // Strip trailing slashes if present because trailing slash will be appended to redirect URL
        this.spaBaseUrl = spaBaseUrl.endsWith("/") ? spaBaseUrl.substring(0, spaBaseUrl.length() - 1) : spaBaseUrl;
        this.csrfTokenRepository = csrfTokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Generate the CSRF Token when a login has been authenticated, so that the user has access to the token immediately in order to make POST requests
        CsrfToken csrfToken = csrfTokenRepository.generateToken((request));
        csrfTokenRepository.saveToken(csrfToken, request, response);
        response.sendRedirect(spaBaseUrl + "/");
    }

}
