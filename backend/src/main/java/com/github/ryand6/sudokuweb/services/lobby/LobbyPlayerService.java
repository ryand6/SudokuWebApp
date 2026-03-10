package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyPlayerStatusUpdatedEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.UpdateLobbyCountdownSchedulerEvent;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class LobbyPlayerService {

    private final LobbyService lobbyService;
    private final LobbyChatService lobbyChatService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final LobbyCountdownMutationService lobbyCountdownMutationService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public LobbyPlayerService(LobbyService lobbyService,
                              LobbyChatService lobbyChatService,
                              LobbyEntityDtoMapper lobbyEntityDtoMapper,
                              LobbyCountdownMutationService lobbyCountdownMutationService,
                              ApplicationEventPublisher applicationEventPublisher) {
        this.lobbyService = lobbyService;
        this.lobbyChatService = lobbyChatService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.lobbyCountdownMutationService = lobbyCountdownMutationService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional
    public LobbyDto updateLobbyPlayerStatus(Long lobbyId, Long userId, LobbyStatus lobbyStatus) {
        LobbyEntity lobby = lobbyService.getLobbyById(lobbyId);
        LobbyPlayerEntity lobbyPlayer = lobbyService.findLobbyPlayer(lobby, userId);
        lobbyPlayer.validateStatusChange();
        // Lobby Player managed by JPA therefore update will apply
        lobbyPlayer.setStatus(lobbyStatus);
        // Handle any countdown updates that may be required
        CountdownEvaluationResult countdownEvaluationResult = lobbyCountdownMutationService.safeEvaluateCountdown(lobby.getLobbyCountdownEntity());

        LobbyDto lobbyDto = lobbyEntityDtoMapper.mapToDto(lobby);

        // Send Lobby Update WS event after commit
        applicationEventPublisher.publishEvent(
                new LobbyPlayerStatusUpdatedEvent(lobbyDto)
        );

        // update countdown scheduler via synchronised event
        applicationEventPublisher.publishEvent(
                new UpdateLobbyCountdownSchedulerEvent(lobbyId, countdownEvaluationResult)
        );

        String message = "updated their status to " + lobbyStatus.toString().toLowerCase() + ".";
        lobbyChatService.createInfoMessage(lobbyId, userId, message, false);

        if (countdownEvaluationResult.getNewInitiator() != null) {
            lobbyChatService.createInfoMessage(lobbyId, countdownEvaluationResult.getNewInitiator(), "started the new game countdown.", false);
        }

        return lobbyDto;
    }


}
