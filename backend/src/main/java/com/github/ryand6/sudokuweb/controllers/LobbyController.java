package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.dto.PrivateLobbyJoinRequestDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.*;
import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    private final UserService userService;

    public LobbyController(LobbyService lobbyService,
                           UserService userService) {
        this.lobbyService = lobbyService;
        this.userService = userService;
    }

    // Render lobby creation view
    @GetMapping("/lobby/create-lobby")
    public String createLobbyView() {
        return "lobby/create-lobby"; // Render view "templates/lobby/create-lobby.html"
    }

    // Render lobby view
    @GetMapping("/lobby/{lobbyId}")
    public String getLobby(@PathVariable Long lobbyId,
                           @AuthenticationPrincipal OAuth2User principal,
                           OAuth2AuthenticationToken authToken,
                           Model model) {
        UserDto userDto = userService.getCurrentUserByOAuth(principal, authToken);
        if (userDto == null) {
            throw new UserNotFoundException("User not found via OAuth token");
        } else {
            LobbyDto lobby = lobbyService.getLobbyById(lobbyId);
            if (lobby == null) {
                throw new LobbyNotFoundException("Lobby with ID " + lobbyId + " not found");
            }
            model.addAttribute("lobby", lobby); // Pass the lobby DTO as context data
            model.addAttribute("requesterId", userDto.getId()); // Pass the user ID of the requester accessing the lobby
            return "lobby/lobby"; // Return lobby.html
        }
    }

    // Post form details to create Lobby in DB
    @PostMapping("/lobby/process-lobby-setup")
    public String processLobbySetupRequest(@AuthenticationPrincipal OAuth2User principal,
                                           OAuth2AuthenticationToken authToken,
                                           @RequestParam(name = "lobbyName") String lobbyName,
                                           @RequestParam(name = "isPublic", required = false) Boolean isPublic,
                                           @RequestParam(name = "isPrivate", required = false) Boolean isPrivate,
                                           RedirectAttributes redirectAttributes) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        if (currentUser == null) {
            throw new UserNotFoundException("User not found via OAuth token");
        }
        try {
            // Create lobby in DB then go to that corresponding lobby's view
            LobbyDto lobbyDto = lobbyService.createNewLobby(lobbyName, isPublic, isPrivate, currentUser.getId());
            return "redirect:/lobby/" + lobbyDto.getId();
        } catch (InvalidLobbyPublicStatusParametersException invalidLobbyPublicStatusParametersException) {
            redirectAttributes.addFlashAttribute("errorMessage", invalidLobbyPublicStatusParametersException.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred when trying to create Lobby");
        }
        return "redirect:/lobby/create-lobby";
    }


    // Return list of up to 10x lobbies
    @GetMapping("/lobby/public/get-active-lobbies")
    @ResponseBody
    public List<LobbyDto> getPublicLobbies(@RequestParam int page) {
        return lobbyService.getPublicLobbies(page, 10);
    }

    // Attempt to join a public lobby, failures could result in lobby now being full or being inactive (closed)
    @PostMapping("/lobby/join/public/{lobbyId}")
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
    @PostMapping("/lobby/join/private")
    public String attemptJoinPrivateLobbyViaForm(
                                   @AuthenticationPrincipal OAuth2User principal,
                                   OAuth2AuthenticationToken authToken,
                                   PrivateLobbyJoinRequestDto joinRequest,
                                   RedirectAttributes redirectAttributes) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        if (currentUser == null) {
            throw new UserNotFoundException("User not found via OAuth token");
        }
        try {
            LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), joinRequest.getToken());

            /* Need to add WebSocket messaging to update lobby view in real time */

            return "redirect:/lobby/" + lobbyDto.getId();
        // Catch and handle any Lobby state related exceptions
        } catch (LobbyFullException | LobbyInactiveException | LobbyNotFoundException |
                 InvalidTokenException | TokenNotFoundException lobbyStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", lobbyStateException.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred when trying to join Lobby");
        }
        return "redirect:/dashboard";
    }

    // Attempt to join a private lobby by sending a token via the URL string, failures could result in lobby now being full or being inactive, token being used or invalid
    @PostMapping("/lobby/join/private/{token}")
    public String attemptJoinPrivateLobbyViaTokenInURL(
            @AuthenticationPrincipal OAuth2User principal,
            OAuth2AuthenticationToken authToken,
            @PathVariable String token,
            RedirectAttributes redirectAttributes) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        if (currentUser == null) {
            throw new UserNotFoundException("User not found via OAuth token");
        }
        try {
            LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), token);

            /* Need to add WebSocket messaging to update lobby view in real time */

            return "redirect:/lobby/" + lobbyDto.getId();
            // Catch and handle any Lobby state related exceptions
        } catch (LobbyFullException | LobbyInactiveException | LobbyNotFoundException |
                 InvalidTokenException | TokenNotFoundException lobbyStateException) {
            redirectAttributes.addFlashAttribute("errorMessage", lobbyStateException.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unexpected error occurred when trying to join Lobby");
        }
        return "redirect:/dashboard";
    }

    // Make a POST request to alter Lobby and LobbyPlayer DB tables when a user leaves the lobby, either through disconnecting or manually leaving
    @PostMapping("/lobby/leave")
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
