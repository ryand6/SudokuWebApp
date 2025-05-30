package com.github.ryand6.sudokuweb.util;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public final class OAuthUtil {

    private OAuthUtil() {

    }

    public static String retrieveOAuthProviderName(OAuth2AuthenticationToken authToken) {
        return authToken.getAuthorizedClientRegistrationId();
    }

    public static String retrieveOAuthProviderId(String providerName, OAuth2User principal) {
        String providerId = switch (providerName) {
            case "google" -> principal.getName();
            case "github", "facebook" -> principal.getAttribute("id").toString();
            default -> throw new IllegalStateException("Unknown provider: " + providerName);
        };
        return providerId;
    }

}
