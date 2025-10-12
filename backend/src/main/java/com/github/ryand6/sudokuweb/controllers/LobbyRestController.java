package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.dto.request.LobbySetupRequestDto;
import com.github.ryand6.sudokuweb.dto.request.PrivateLobbyJoinRequestDto;
import com.github.ryand6.sudokuweb.dto.entity.UserDto;
import com.github.ryand6.sudokuweb.dto.response.PublicLobbiesListDto;
import com.github.ryand6.sudokuweb.exceptions.*;
import com.github.ryand6.sudokuweb.services.LobbyService;
import com.github.ryand6.sudokuweb.services.UserService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/lobby")
public class LobbyRestController {

    private final LobbyService lobbyService;
    private final UserService userService;

    public LobbyRestController(LobbyService lobbyService,
                               UserService userService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
    }

    // Render lobby view
//    @GetMapping("/{lobbyId}")
//    public String getLobby(@PathVariable Long lobbyId,
//                           @AuthenticationPrincipal OAuth2User principal,
//                           OAuth2AuthenticationToken authToken,
//                           Model model) {
//        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);
//        if (userDto == null) {
//            throw new UserNotFoundException("User not found via OAuth token");
//        } else {
//            LobbyDto lobby = lobbyService.getLobbyById(lobbyId);
//            if (lobby == null) {
//                throw new LobbyNotFoundException("Lobby with ID " + lobbyId + " not found");
//            }
//            model.addAttribute("lobby", lobby); // Pass the lobby DTO as context data
//            model.addAttribute("requesterId", userDto.getId()); // Pass the user ID of the requester accessing the lobby
//            return "lobby/lobby"; // Return lobby.html
//        }
//    }

    // Post form details to create Lobby in DB
    @PostMapping("/process-lobby-setup")
    public ResponseEntity<?> processLobbySetupRequest(@AuthenticationPrincipal OAuth2User principal,
                                                      OAuth2AuthenticationToken authToken,
                                                      @Valid @RequestBody LobbySetupRequestDto lobbySetupRequestDto,
                                                      BindingResult bindingResult) {
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
        String lobbyName = lobbySetupRequestDto.getLobbyName();
        Boolean isPublic = lobbySetupRequestDto.getIsPublic();
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        // Create lobby in DB then go to that corresponding lobby's view
        LobbyDto lobbyDto = lobbyService.createNewLobby(lobbyName, isPublic, currentUser.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lobbyDto);
    }

    // Return list of up to 10x lobbies
    @GetMapping("/public/get-active-lobbies")
    public ResponseEntity<?> getPublicLobbies(@RequestParam int page) {
        List<LobbyDto> publicLobbies = lobbyService.getPublicLobbies(page, 10);
        return ResponseEntity.ok(new PublicLobbiesListDto(publicLobbies));
    }

    // Attempt to join a public lobby, failures could result in lobby now being full or being inactive (closed)
    @PostMapping("/join/public/{lobbyId}")
    public String attemptJoinPublicLobby(
                                    @AuthenticationPrincipal OAuth2User principal,
                                    OAuth2AuthenticationToken authToken,
                                    @PathVariable Long lobbyId,
                                    RedirectAttributes redirectAttributes) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        if (currentUser == null) {
            throw new UserNotFoundException("User not found via OAuth token");
        }
        try {
            LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), lobbyId);

            /* Need to add WebSocket messaging to update lobby view in real time */

            return "redirect:/lobby/" + lobbyDto.getId();
            // Catch and handle any Lobby state related exceptions
        } catch (LobbyFullException | LobbyInactiveException | LobbyNotFoundException |
                 InvalidTokenException lobbyStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", lobbyStateException.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred when trying to join Lobby");
        }
        return "redirect:/dashboard";
    }

    // Attempt to join a private lobby via sending a token using a form, failures could result in lobby now being full or being inactive, token being used or invalid
    @PostMapping("/join/private")
    public ResponseEntity<?> attemptJoinPrivateLobbyViaForm(
                                   @AuthenticationPrincipal OAuth2User principal,
                                   OAuth2AuthenticationToken authToken,
                                   @RequestBody PrivateLobbyJoinRequestDto joinRequest) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), joinRequest.getToken());
        return ResponseEntity.ok(lobbyDto);
    }

    // Attempt to join a private lobby by sending a token via the URL string, failures could result in lobby now being full or being inactive, token being used or invalid
    @PostMapping("/join/private/{token}")
    public ResponseEntity<?> attemptJoinPrivateLobbyViaTokenInURL(
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authToken,
            @PathVariable String token) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), token);
        return ResponseEntity.ok(lobbyDto);
    }

    // Make a POST request to alter Lobby and LobbyPlayer DB tables when a user leaves the lobby, either through disconnecting or manually leaving
    @PostMapping("/leave")
    @ResponseBody
    public LobbyDto leaveLobby(@AuthenticationPrincipal OAuth2User principal,
                               OAuth2AuthenticationToken authToken,
                               @RequestParam Long lobbyId) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);

        try {
            LobbyDto lobbyDto = lobbyService.removeFromLobby(currentUser.getId(), lobbyId);

            /* Need to add WebSocket messaging to update lobby / game view in real time */

            return lobbyDto;
        } catch (Exception e) {
            return null;
        }
    }

}
