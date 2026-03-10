package com.github.ryand6.sudokuweb.controllers.rest.lobby;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyDifficultyUpdateRequestDto;
import com.github.ryand6.sudokuweb.dto.request.LobbyTimeLimitUpdateRequestDto;
import com.github.ryand6.sudokuweb.services.lobby.LobbySettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobby/settings")
public class LobbySettingsRestController {

    private final LobbySettingsService lobbySettingsService;

    public LobbySettingsRestController(LobbySettingsService lobbySettingsService) {
        this.lobbySettingsService = lobbySettingsService;
    }

    // Update difficulty to be applied to lobby games
    @PostMapping("/update-difficulty")
    public ResponseEntity<?> updateLobbyDifficulty(@RequestBody LobbyDifficultyUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbySettingsService.updateLobbyDifficulty(requestDto.getLobbyId(), requestDto.getUserId(), requestDto.getDifficulty());
        return ResponseEntity.ok(lobbyDto);
    }

    // Update time limit to be applied to lobby games
    @PostMapping("/update-time-limit")
    public ResponseEntity<?> updateLobbyTimeLimit(@RequestBody LobbyTimeLimitUpdateRequestDto requestDto) {
        LobbyDto lobbyDto = lobbySettingsService.updateLobbyTimeLimit(requestDto.getLobbyId(), requestDto.getUserId(), requestDto.getTimeLimit());
        return ResponseEntity.ok(lobbyDto);
    }

}
