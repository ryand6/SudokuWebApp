package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Render user-setup view - form for new visitor to create username, creating their User in the DB when submitted
    @GetMapping("/user-setup")
    public String userSetupForm(@AuthenticationPrincipal OAuth2User principal,
                                OAuth2AuthenticationToken authToken) {

        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);
        if (userDto != null) {
            // user is already authenticated/created
            return "redirect:/dashboard";
        }
        return "user-setup";
    }

    // Post form details to create User in DB
    @PostMapping("/process-user-setup")
    public String processUserSetupRequest(@AuthenticationPrincipal OAuth2User principal,
                                          OAuth2AuthenticationToken authToken,
                                          @RequestParam(name = "username") String username,
                                          RedirectAttributes redirectAttributes,
                                          HttpServletRequest request) {
        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);
        if (userDto != null) {
            // user is already authenticated/created
            return "redirect:/dashboard";
        }
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        try {
            // Create user in DB then go to dashboard
            userService.createNewUser(username, provider, providerId);
            // Retrieve original URL user was trying to access
            HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
            SavedRequest savedRequest = requestCache.getRequest(request, null);
            // If original URL is found redirect the user there, otherwise default to dashboard
            String redirectUrl = (savedRequest != null) ? savedRequest.getRedirectUrl() : "/dashboard";
            return "redirect:" + redirectUrl;
            // redirect back to the user-setup view if an error occurs trying to create a User in the DB when submitting the form
        } catch (UsernameTakenException usernameTakenException) {
            redirectAttributes.addFlashAttribute("errorMessage", usernameTakenException.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred when trying to create User");

        }
        return "redirect:/user-setup";
    }

}
