package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.services.impl.AuthenticateUserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final AuthenticateUserService authenticateUserService;

    public LoginController(AuthenticateUserService authenticateUserService) {
        this.authenticateUserService = authenticateUserService;
    }

    @GetMapping("/login-success")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal,
                               OAuth2AuthenticationToken authToken) {
        return authenticateUserService.tryGetCurrentUser(principal, authToken)
                .map(user -> "redirect:/dashboard") // Redirects to main dashboard if user found after login successful
                .orElse("redirect:/setup-user");  // Redirects to user set-up page if login successful but user not created in DB yet
    }

}
