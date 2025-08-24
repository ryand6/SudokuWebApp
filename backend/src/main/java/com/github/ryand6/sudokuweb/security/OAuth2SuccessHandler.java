package com.github.ryand6.sudokuweb.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final String spaBaseUrl;

    public OAuth2SuccessHandler(String spaBaseUrl) {
        // Strip trailing slashes if present because trailing slash will be appended to redirect URL
        this.spaBaseUrl = spaBaseUrl.endsWith("/") ? spaBaseUrl.substring(0, spaBaseUrl.length() - 1) : spaBaseUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.sendRedirect(spaBaseUrl + "/");
    }

}
