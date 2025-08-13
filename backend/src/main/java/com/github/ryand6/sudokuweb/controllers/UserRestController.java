package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.ApiErrorDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.UserNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.UsernameTakenException;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // Get current authenticated user
    @GetMapping("/current-user")
    public ResponseEntity<UserDto> userSetupForm(@AuthenticationPrincipal OAuth2User principal,
                                OAuth2AuthenticationToken authToken) {
        // null if user not returned
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        if (user == null) {
            throw new UserNotFoundException("Current user not found");
        }
        return ResponseEntity.ok(user);
    }

    // Post form details to create User in DB
    @PostMapping("/process-user-setup")
    public ResponseEntity<Void> processUserSetupRequest(@AuthenticationPrincipal OAuth2User principal,
                                                  OAuth2AuthenticationToken authToken,
                                                  @RequestParam(name = "username") String username,
                                                  RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request) {
        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        // Create user in DB
        userService.createNewUser(username, provider, providerId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }

}
