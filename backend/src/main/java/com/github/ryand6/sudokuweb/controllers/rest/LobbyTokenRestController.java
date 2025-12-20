package com.github.ryand6.sudokuweb.controllers.rest;

import com.github.ryand6.sudokuweb.dto.request.RequestLobbyTokenDto;
import com.github.ryand6.sudokuweb.dto.response.LobbyTokenResponseDto;
import com.github.ryand6.sudokuweb.dto.response.UserActiveTokensDto;
import com.github.ryand6.sudokuweb.services.PrivateLobbyTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lobby/token")
public class LobbyTokenRestController {

    private final PrivateLobbyTokenService privateLobbyTokenService;

    public LobbyTokenRestController(PrivateLobbyTokenService privateLobbyTokenService) {
        this.privateLobbyTokenService = privateLobbyTokenService;
    }

    @PostMapping("/request-token")
    public ResponseEntity<?> requestNewToken(@RequestBody RequestLobbyTokenDto requestDto) {
        Long lobbyId = requestDto.getLobbyId();
        Long userId = requestDto.getUserId();
        String token = privateLobbyTokenService.generateToken(lobbyId, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new LobbyTokenResponseDto(token));
    }

    @GetMapping("/{userId}/get-active-tokens")
    public ResponseEntity<?> getActiveTokens(@PathVariable Long userId) {
        UserActiveTokensDto activeTokens = privateLobbyTokenService.getActiveTokensForUser(userId);
        return ResponseEntity.ok(activeTokens);
    }

}
