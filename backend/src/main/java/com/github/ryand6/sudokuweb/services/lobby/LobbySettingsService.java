package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import com.github.ryand6.sudokuweb.exceptions.lobby.settings.LobbySettingsLockedException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LobbySettingsService {

    private final LobbyService lobbyService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;

    public LobbySettingsService(LobbyService lobbyService, LobbyEntityDtoMapper lobbyEntityDtoMapper) {
        this.lobbyService = lobbyService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
    }

    @Transactional
    public LobbyDto updateLobbyDifficulty(Long lobbyId, Difficulty difficulty) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        if (lobby.getLobbyCountdownEntity().isCountdownActive()) {
            throw new LobbySettingsLockedException("Cannot update settings whilst the countdown is active.");
        }
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.setDifficulty(difficulty);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    @Transactional
    public LobbyDto updateLobbyTimeLimit(Long lobbyId, TimeLimitPreset timeLimit) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        if (lobby.getLobbyCountdownEntity().isCountdownActive()) {
            throw new LobbySettingsLockedException("Cannot update settings whilst the countdown is active.");
        }
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.setTimeLimit(timeLimit);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

}
