package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Render user-setup view - form for new visitor to create username, creating their User in the DB when submitted
    @GetMapping("/user-setup")
    public String userSetupForm() {
        return "user-setup";
    }

    // Post form details to create User in DB
    @PostMapping("/process-user-setup")
    public String processUserSetupRequest(@AuthenticationPrincipal OAuth2User principal,
                                          OAuth2AuthenticationToken authToken,
                                          @RequestParam String username,
                                          RedirectAttributes redirectAttributes) {
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        try {
            // Create user in DB then go to dashboard
            userService.createNewUser(username, provider, providerId);
            return "redirect:/dashboard";
            // redirect back to the user-setup view if an error occurs trying to create a User in the DB when submitting the form
        } catch (UsernameTakenException usernameTakenException) {
            redirectAttributes.addFlashAttribute("errorMessage", usernameTakenException.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred when trying to create User");

        }
        return "redirect:/user-setup";
    }

    @GetMapping("/dashboard")
    public String getUserDashboard(@AuthenticationPrincipal OAuth2User principal,
                                   OAuth2AuthenticationToken authToken,
                                   Model model) {
        Optional<UserEntity> userOptional = userService.tryGetCurrentUser(principal, authToken);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            model.addAttribute("user", user); // Pass the user entity instance as context data
            return "dashboard"; // Return dashboard.html
        } else {
            return "redirect:/user-not-found";
        }
    }

}
