package com.github.ryand6.sudokuweb.security;

import com.github.ryand6.sudokuweb.services.user.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final String spaBaseUrl;
    private final CookieCsrfTokenRepository csrfTokenRepository;
    private final UserService userService;
    private static final long PROVIDER_LINK_TIMEOUT_MS = 10 * 60 * 1000;

    public OAuth2SuccessHandler(String spaBaseUrl,
                                CookieCsrfTokenRepository csrfTokenRepository,
                                UserService userService) {
        // Strip trailing slashes if present because trailing slash will be appended to redirect URL
        this.spaBaseUrl = spaBaseUrl.endsWith("/") ? spaBaseUrl.substring(0, spaBaseUrl.length() - 1) : spaBaseUrl;
        this.csrfTokenRepository = csrfTokenRepository;
        this.userService = userService;
    }

    /**
     * Generates CSRF token when a login has been authenticated - provides user with access to token immediately in order to make POST requests
     * @param request The HTTP request
     * @param response The HTTP response
     * @param authentication The token for an authenticated principal
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        HttpSession session = request.getSession();
        Long pendingUserId = (Long) session.getAttribute("pendingLinkProviderUserId");
        String pendingProviderName = (String) session.getAttribute("pendingLinkProviderName");
        Long createdAt = (Long) session.getAttribute("pendingLinkProviderCreatedAt");
        if (!isValidProviderLinkRequest(pendingUserId, pendingProviderName, createdAt)) {
            clearProviderLinkSession(session);
            redirectNormally(request, response);
            return;
        }
        try {
            if (!(authentication instanceof OAuth2AuthenticationToken authToken)) {
                throw new IllegalStateException("Expected OAuth2AuthenticationToken");
            }
            OAuth2User principal = (OAuth2User) authentication.getPrincipal();
            String actualProvider = OAuthUtil.retrieveOAuthProviderName(authToken);
            String actualProviderId = OAuthUtil.retrieveOAuthProviderId(actualProvider, principal);
            if (!pendingProviderName.equals(actualProvider)) {
                throw new IllegalStateException("OAuth provider does not match provider-link request");
            }
            userService.completeProviderLink(pendingUserId, actualProvider, actualProviderId);
            clearProviderLinkSession(session);
            saveCsrfToken(request, response);
            response.sendRedirect(spaBaseUrl + "/link-additional-providers");
        } catch (Exception exception) {
            clearProviderLinkSession(session);
            response.sendRedirect(spaBaseUrl + "/link-additional-providers?linkError");
        }
    }

    private boolean isValidProviderLinkRequest(Long userId,
                                               String provider,
                                               Long createdAt) {
        if (userId == null || provider == null || createdAt == null) {
            return false;
        }
        return System.currentTimeMillis() - createdAt
                <= PROVIDER_LINK_TIMEOUT_MS;
    }

    private void clearProviderLinkSession(HttpSession session) {
        session.removeAttribute("pendingLinkProviderUserId");
        session.removeAttribute("pendingLinkProviderName");
        session.removeAttribute("pendingLinkProviderCreatedAt");
    }

    private void saveCsrfToken(HttpServletRequest request,
                               HttpServletResponse response) {
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrfToken, request, response);
    }

    private void redirectNormally(HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        saveCsrfToken(request, response);
        response.sendRedirect(spaBaseUrl + "/");
    }

}
