package com.github.ryand6.sudokuweb;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

public final class TestOAuthUtil {

    /*
    Dummy OAuth token used for test purposes only to satisfy the requirements of OAuthUtil static methods which can't be easily mocked
     */
    public static OAuth2AuthenticationToken createOAuthToken() {
        Map<String, Object> attributes = Map.of(
                "sub", "1234567890",
                "id", 1234567890
        );
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        OAuth2User principal = new DefaultOAuth2User(authorities, attributes, "sub");

        return new OAuth2AuthenticationToken(principal, authorities, "google");
    }

}
