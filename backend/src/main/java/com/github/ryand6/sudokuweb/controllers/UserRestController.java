package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.dto.UserSetupRequestDto;
import com.github.ryand6.sudokuweb.exceptions.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.exceptions.UserNotFoundException;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        // OAuth2 login not occurred yet
        if (principal == null || authToken == null) {
            throw new OAuth2LoginRequiredException("OAuth2 login required to carry out this action");
        }
        // null if user not returned
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        if (user == null) {
            throw new UserNotFoundException("Current user not found");
        }
        return ResponseEntity.ok(user);
    }

    // Post form details to create User in DB
    @PostMapping("/process-user-setup")
    public ResponseEntity<?> processUserSetupRequest(@AuthenticationPrincipal OAuth2User principal,
                                                        OAuth2AuthenticationToken authToken,
                                                        @Valid @RequestBody UserSetupRequestDto request,
                                                        BindingResult bindingResult) {
        // Check if validation errors occurred in DTO
        if (bindingResult.hasErrors()) {
            // Collect all validation errors
            List<String> errors = bindingResult.getAllErrors()
                                                .stream()
                                                .map(ObjectError::getDefaultMessage)
                                                .toList();
            return ResponseEntity
                    .badRequest()
                    .body(errors);
        }

        String provider = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(provider, principal);
        String username = request.getUsername();
        // Create user in DB
        userService.createNewUser(username, provider, providerId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(null);
    }

}
