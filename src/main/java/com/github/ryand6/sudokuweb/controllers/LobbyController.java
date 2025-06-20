package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.exceptions.InvalidLobbyPublicStatusParametersException;
import com.github.ryand6.sudokuweb.exceptions.UserNotFoundException;
import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import com.github.ryand6.sudokuweb.services.impl.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
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

    // Retrieve unique join code from backend
    @GetMapping("/lobby/generate-join-code")
    public ResponseEntity<String> generateJoinCode() {
        String code = lobbyService.generateUniqueCode();
        return ResponseEntity.ok(code);
    }

    // Post form details to create Lobby in DB
    @PostMapping("/lobby/process-lobby-setup")
    public String processLobbySetupRequest(@AuthenticationPrincipal OAuth2User principal,
                                           OAuth2AuthenticationToken authToken,
                                           @RequestParam(name = "lobbyName") String lobbyName,
                                           @RequestParam(name = "isPublic", required = false) Boolean isPublic,
                                           @RequestParam(name = "isPrivate", required = false) Boolean isPrivate,
                                           @RequestParam(name = "joinCode", required = false) String joinCode,
                                           RedirectAttributes redirectAttributes) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        if (currentUser == null) {
            throw new UserNotFoundException("User not found via OAuth token");
        }
        try {
            // Create lobby in DB then go to that corresponding lobby's view
            LobbyDto lobbyDto = lobbyService.createNewLobby(lobbyName, isPublic, isPrivate, joinCode, currentUser.getId());
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
    public List<LobbyDto> getPublicLobbies(@RequestParam int page) {
        return lobbyService.getPublicLobbies(page, 10);
    }

}
