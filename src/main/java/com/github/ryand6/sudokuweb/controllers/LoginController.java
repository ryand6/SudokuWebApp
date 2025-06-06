package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // Handle where to redirect if the OAuth2 login is successful
    @GetMapping("/login-success")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal,
                               OAuth2AuthenticationToken authToken) {

        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);

        if (userDto == null) {
            return "redirect:/user-setup"; // Redirects to user set-up page if login successful but user not created in DB yet
        } else {
            return "redirect:/dashboard"; // Redirects to main dashboard if user found after login successful
        }
    }

}
