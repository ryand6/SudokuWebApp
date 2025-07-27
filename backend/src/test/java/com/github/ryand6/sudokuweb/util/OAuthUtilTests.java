package com.github.ryand6.sudokuweb.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OAuthUtilTests {

    @Test
    void testRetrieveOAuthProviderId() {
        OAuth2AuthenticationToken token = mock(OAuth2AuthenticationToken.class);
        when(token.getAuthorizedClientRegistrationId()).thenReturn("google");

        String provider = OAuthUtil.retrieveOAuthProviderName(token);
        assertEquals("google", provider);
    }

    @Test
    void retrieveOAuthProviderId_testGoogle() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getName()).thenReturn("google-123");

        String providerId = OAuthUtil.retrieveOAuthProviderId("google", principal);
        assertEquals("google-123", providerId);
    }

    @Test
    void retrieveOAuthProviderId_testGithub() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("id")).thenReturn("github-123");

        String providerId = OAuthUtil.retrieveOAuthProviderId("github", principal);
        assertEquals("github-123", providerId);
    }

    @Test
    void retrieveOAuthProviderId_testFacebook() {
        OAuth2User principal = mock(OAuth2User.class);
        when(principal.getAttribute("id")).thenReturn("facebook-123");

        String providerId = OAuthUtil.retrieveOAuthProviderId("facebook", principal);
        assertEquals("facebook-123", providerId);
    }

}
