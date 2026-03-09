package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import com.github.ryand6.sudokuweb.exceptions.lobby.settings.LobbySettingsLockedException;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class LobbySettingsService {

    private final LobbyService lobbyService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;

    public LobbySettingsService(LobbyService lobbyService, LobbyEntityDtoMapper lobbyEntityDtoMapper) {
        this.lobbyService = lobbyService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public LobbyDto updateLobbyDifficulty(Long lobbyId, Difficulty difficulty) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.validateSettingsUpdates();
        lobbySettings.setDifficulty(difficulty);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Fallback if retries fail for updateLobbyDifficulty
    @Recover
    public LobbyDto updateLobbyDifficultyRecover(ObjectOptimisticLockingFailureException ex, Long lobbyId, Difficulty difficulty) {
        throw new LobbySettingsLockedException("Cannot update settings currently due to a conflict. Please try again shortly.");
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public LobbyDto updateLobbyTimeLimit(Long lobbyId, TimeLimitPreset timeLimit) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.validateSettingsUpdates();
        lobbySettings.setTimeLimit(timeLimit);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Fallback if retries fail for updateLobbyTimeLimit
    @Recover
    public LobbyDto updateLobbyTimeLimitRecover(ObjectOptimisticLockingFailureException ex, Long lobbyId, TimeLimitPreset timeLimit) {
        throw new LobbySettingsLockedException("Cannot update settings currently due to a conflict. Please try again shortly.");
    }

}
