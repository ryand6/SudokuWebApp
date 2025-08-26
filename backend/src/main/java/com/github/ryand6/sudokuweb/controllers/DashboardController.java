package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.UserNotFoundException;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    // Render dashboard view
    @GetMapping("/api/dashboard")
    public String getUserDashboard(@AuthenticationPrincipal OAuth2User principal,
                                   OAuth2AuthenticationToken authToken,
                                   Model model) {
        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);

        if (userDto == null) {
            throw new UserNotFoundException("User not found via OAuth token");
        } else {
            List<UserDto> topPlayers = userService.getTop5PlayersTotalScore();
            Long userRank = userService.getPlayerRank(userDto.getId());
            model.addAttribute("user", userDto); // Pass the user DTO as context data
            model.addAttribute("topPlayers", topPlayers);
            model.addAttribute("userRank", userRank);
            return "dashboard"; // Return dashboard.html
        }
    }

}
