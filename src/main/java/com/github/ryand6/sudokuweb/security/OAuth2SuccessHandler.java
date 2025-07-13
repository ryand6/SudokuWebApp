package com.github.ryand6.sudokuweb.security;

import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    public OAuth2SuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User principal = authToken.getPrincipal();
        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);
        if (userDto == null) {
            getRedirectStrategy().sendRedirect(request, response, "/user-setup");
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

}
