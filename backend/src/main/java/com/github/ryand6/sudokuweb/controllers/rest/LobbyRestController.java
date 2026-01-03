package com.github.ryand6.sudokuweb.controllers.rest;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.dto.request.*;
import com.github.ryand6.sudokuweb.dto.entity.UserDto;
import com.github.ryand6.sudokuweb.dto.response.PublicLobbiesListDto;
import com.github.ryand6.sudokuweb.services.LobbyService;
import com.github.ryand6.sudokuweb.services.LobbyWebSocketsService;
import com.github.ryand6.sudokuweb.services.UserService;
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

@RestController
@RequestMapping("/api/lobby")
public class LobbyRestController {

    private final LobbyService lobbyService;
    private final UserService userService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyRestController(LobbyService lobbyService,
                               UserService userService,
                               LobbyWebSocketsService lobbyWebSocketsService,
                               SimpMessagingTemplate messagingTemplate) {
        this.lobbyService = lobbyService;
        this.userService = userService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.messagingTemplate = messagingTemplate;
    }

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

    // Get lobby DTO using lobbyId
    @GetMapping("/get-lobby")
    public ResponseEntity<?> getLobby(@RequestParam Long lobbyId) {
        LobbyDto lobbyDto = lobbyService.getLobbyById(lobbyId);
        return ResponseEntity.ok(lobbyDto);
    }

    // Return list of up to 10x lobbies
    @GetMapping("/public/get-active-lobbies")
    public ResponseEntity<?> getPublicLobbies(@RequestParam int page) {
        List<LobbyDto> publicLobbies = lobbyService.getPublicLobbies(page, 10);
        return ResponseEntity.ok(new PublicLobbiesListDto(publicLobbies));
    }

    // Attempt to join a public lobby, failures could result in lobby now being full or being inactive (closed)
    @PostMapping("/join/public/{lobbyId}")
    public ResponseEntity<?> joinPublicLobby(
                                    @AuthenticationPrincipal OAuth2User principal,
                                    OAuth2AuthenticationToken authToken,
                                    @PathVariable Long lobbyId) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), lobbyId);

        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);

        return ResponseEntity.ok(lobbyDto);
    }

    // Attempt to join a private lobby via sending a token using a form, failures could result in lobby now being full or being inactive, token being used or invalid
    @PostMapping("/join/private")
    public ResponseEntity<?> joinPrivateLobby(
                                   @AuthenticationPrincipal OAuth2User principal,
                                   OAuth2AuthenticationToken authToken,
                                   @RequestBody PrivateLobbyJoinRequestDto joinRequest) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), joinRequest.getToken());

        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);

        return ResponseEntity.ok(lobbyDto);
    }

    // Attempt to join a private lobby by sending a token via the URL string, failures could result in lobby now being full or being inactive, token being used or invalid
//    @PostMapping("/join/private/{token}")
//    public ResponseEntity<?> joinPrivateLobbyViaTokenInURL(
//            @AuthenticationPrincipal OAuth2User principal,
//            OAuth2AuthenticationToken authToken,
//            @PathVariable String token) {
//        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
//        LobbyDto lobbyDto = lobbyService.joinLobby(currentUser.getId(), token);
//        return ResponseEntity.ok(lobbyDto);
//    }

    // Make a POST request to alter Lobby and LobbyPlayer DB tables when a user leaves the lobby, either through disconnecting or manually leaving
    @PostMapping("/leave")
    public ResponseEntity<?> leaveLobby(@AuthenticationPrincipal OAuth2User principal,
                               OAuth2AuthenticationToken authToken,
                               @Valid @RequestBody LeaveLobbyRequestDto requestDto) {
        UserDto currentUser = userService.getCurrentUserByOAuth(principal, authToken);
        LobbyDto lobbyDto = lobbyService.removeFromLobby(requestDto.getLobbyId(), currentUser.getId());

        if (lobbyDto == null) {
            return ResponseEntity.noContent().build();
        }

        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);

        return ResponseEntity.ok(lobbyDto);
    }


    // Update difficulty to be applied to lobby games
    @PostMapping("/update-difficulty")
    public ResponseEntity<?> updateLobbyDifficulty(@RequestBody LobbyDifficultyUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbyService.updateLobbyDifficulty(requestDto.getLobbyId(), requestDto.getDifficulty());
        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);
        return ResponseEntity.ok(lobbyDto);
    }

    // Update time limit to be applied to lobby games
    @PostMapping("/update-time-limit")
    public ResponseEntity<?> updateLobbyTimeLimit(@RequestBody LobbyTimeLimitUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbyService.updateLobbyTimeLimit(requestDto.getLobbyId(), requestDto.getTimeLimit());
        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);
        return ResponseEntity.ok(lobbyDto);
    }

    // Update a lobby player's status
    @PostMapping("/update-player-status")
    public ResponseEntity<?> updateLobbyPlayerStatus(@RequestBody LobbyPlayerStatusUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbyService.updateLobbyPlayerStatus(requestDto.getLobbyId(), requestDto.getUserId(), requestDto.getLobbyStatus());
        lobbyWebSocketsService.handleLobbyUpdate(lobbyDto, messagingTemplate);
        return ResponseEntity.ok(lobbyDto);
    }

}
