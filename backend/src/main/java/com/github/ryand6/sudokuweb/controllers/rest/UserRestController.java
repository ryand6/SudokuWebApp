package com.github.ryand6.sudokuweb.controllers.rest;

import com.github.ryand6.sudokuweb.dto.entity.UserDto;
import com.github.ryand6.sudokuweb.dto.request.UserSetupRequestDto;
import com.github.ryand6.sudokuweb.dto.response.TopFivePlayersDto;
import com.github.ryand6.sudokuweb.dto.response.UserRankDto;
import com.github.ryand6.sudokuweb.exceptions.auth.OAuth2LoginRequiredException;
import com.github.ryand6.sudokuweb.services.UserService;
import com.github.ryand6.sudokuweb.util.OAuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final SimpMessagingTemplate  messagingTemplate;

    public UserRestController(UserService userService,
                              SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
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

    // Post update form details to amend User in DB
    @PostMapping("/process-user-amend")
    public ResponseEntity<?> processUserAmendRequest(@AuthenticationPrincipal OAuth2User principal,
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
        String username = request.getUsername();
        // Update user in DB
        UserDto updatedUser = userService.updateUsername(username, principal, authToken);

        // Get providerId so that updated Dto can be sent to correct user via websockets
        String providerName = OAuthUtil.retrieveOAuthProviderName(authToken);
        String providerId = OAuthUtil.retrieveOAuthProviderId(providerName, principal);

        // Create a message header detailing what type of update this is so that the frontend can respond accordingly
        Map<String, Object> messageHeader = Map.of(
                "type", "USER_UPDATED",
                "payload", updatedUser
        );

        // Send the updated user Dto over websockets to that user's topic - Spring automatically prefixes path with "user" and maps to the current user using the providerId
        messagingTemplate.convertAndSendToUser(providerId, "/queue/updates", messageHeader);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(null);
    }

    @GetMapping("/get-user-rank")
    public ResponseEntity<?> getUserRank(@AuthenticationPrincipal OAuth2User principal,
                                           OAuth2AuthenticationToken authToken) {
        UserDto user = userService.getCurrentUserByOAuth(principal, authToken);
        Long playerRank = userService.getPlayerRank(user.getId());
        return ResponseEntity.ok(new UserRankDto(playerRank));
    }

    @GetMapping("/get-top-five-players")
    public ResponseEntity<?> getTopFivePlayers() {
        List<UserDto> topFive = userService.getTop5PlayersTotalScore();
        return ResponseEntity.ok(new TopFivePlayersDto(topFive));
    }
}
