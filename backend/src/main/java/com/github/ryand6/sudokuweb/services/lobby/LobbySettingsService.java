package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import com.github.ryand6.sudokuweb.events.types.lobby.ws.LobbyDifficultySettingsUpdatedWsEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.ws.LobbyTimeLimitSettingsUpdatedWsEvent;
import com.github.ryand6.sudokuweb.exceptions.lobby.settings.LobbySettingsLockedException;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class LobbySettingsService {

    private final LobbyService lobbyService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final LobbyChatService lobbyChatService;
    private final ApplicationEventPublisher applicationEventPublisher;


    public LobbySettingsService(LobbyService lobbyService,
                                LobbyEntityDtoMapper lobbyEntityDtoMapper,
                                LobbyChatService lobbyChatService,
                                ApplicationEventPublisher applicationEventPublisher) {
        this.lobbyService = lobbyService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.lobbyChatService = lobbyChatService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public LobbyDto updateLobbyDifficulty(Long lobbyId, Long userId, Difficulty difficulty) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.validateSettingsUpdates();
        lobbySettings.setDifficulty(difficulty);

        LobbyDto lobbyDto = lobbyEntityDtoMapper.mapToDto(lobby);

        // Send Lobby Update WS event after commit
        applicationEventPublisher.publishEvent(
                new LobbyDifficultySettingsUpdatedWsEvent(lobbyDto)
        );

        // Send an info update to the lobby chat after commit
        String message = "updated the difficulty to " + difficulty.toString().toLowerCase() + ".";
        lobbyChatService.createInfoMessage(lobbyId, userId, message, false);

        return lobbyDto;
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
    public LobbyDto updateLobbyTimeLimit(Long lobbyId, Long userId, TimeLimitPreset timeLimit) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.validateSettingsUpdates();
        lobbySettings.setTimeLimit(timeLimit);

        LobbyDto lobbyDto = lobbyEntityDtoMapper.mapToDto(lobby);

        // Send Lobby Update WS event after commit
        applicationEventPublisher.publishEvent(
                new LobbyTimeLimitSettingsUpdatedWsEvent(lobbyDto)
        );

        // Send an info update to the lobby chat after commit
        String message = "updated the time limit to " + timeLimit.toString().toLowerCase() + ".";
        lobbyChatService.createInfoMessage(lobbyId, userId, message, false);

        return lobbyDto;
    }

    // Fallback if retries fail for updateLobbyTimeLimit
    @Recover
    public LobbyDto updateLobbyTimeLimitRecover(ObjectOptimisticLockingFailureException ex, Long lobbyId, TimeLimitPreset timeLimit) {
        throw new LobbySettingsLockedException("Cannot update settings currently due to a conflict. Please try again shortly.");
    }

}
