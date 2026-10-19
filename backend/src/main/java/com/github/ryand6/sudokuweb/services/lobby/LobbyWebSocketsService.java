package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyGameCreationUpdate;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerJoinedUpdate;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerLeftUpdate;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerStatusUpdate;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbyDifficultyUpdate;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbyTimeLimitUpdate;
import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyGameEndedEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.ws.*;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Service
public class LobbyWebSocketsService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public LobbyWebSocketsService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void handleLobbyDifficultySettingsUpdate(LobbyDifficultyUpdate lobbyDifficultyUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_DIFFICULTY_UPDATED",
                "payload", lobbyDifficultyUpdate
        );
        String topic = "/topic/lobby/" + lobbyDifficultyUpdate.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyTimeLimitSettingsUpdate(LobbyTimeLimitUpdate lobbyTimeLimitUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_TIME_LIMIT_UPDATED",
                "payload", lobbyTimeLimitUpdate
        );
        String topic = "/topic/lobby/" + lobbyTimeLimitUpdate.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyPlayerStatusUpdate(LobbyPlayerStatusUpdate lobbyPlayerStatusUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_PLAYER_STATUS_UPDATED",
                "payload", lobbyPlayerStatusUpdate
        );
        String topic = "/topic/lobby/" + lobbyPlayerStatusUpdate.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyPlayerJoinedUpdate(LobbyPlayerJoinedUpdate lobbyPlayerJoinedUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_PLAYER_JOINED",
                "payload", lobbyPlayerJoinedUpdate
        );
        String topic = "/topic/lobby/" + lobbyPlayerJoinedUpdate.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyPlayerLeftUpdate(LobbyPlayerLeftUpdate lobbyPlayerLeftUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_PLAYER_LEFT",
                "payload", lobbyPlayerLeftUpdate
        );
        String topic = "/topic/lobby/" + lobbyPlayerLeftUpdate.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyGameCreatedUpdate(LobbyGameCreationUpdate lobbyGameCreationUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_GAME_CREATED",
                "payload", lobbyGameCreationUpdate
        );
        String topic = "/topic/lobby/" + lobbyGameCreationUpdate.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyChatMessageSend(LobbyChatMessageDto lobbyChatMessageDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_CHAT_MESSAGE",
                "payload", lobbyChatMessageDto
        );
        String topic = "/topic/lobby/" + lobbyChatMessageDto.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyGameStart(GameDto gameDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_CREATED",
                "payload", gameDto
        );
        String topic = "/topic/lobby/" + gameDto.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyGameEnded(LobbyGameEndedEvent event) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_ENDED",
                "payload", event
        );
        String topic = "/topic/lobby/" + event.getLobbyId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyDifficultySettingsUpdatedWsEvent(LobbyDifficultySettingsUpdatedWsEvent event) {
        handleLobbyDifficultySettingsUpdate(event.getLobbyDifficultyUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyTimeLimitSettingsUpdatedWsEvent(LobbyTimeLimitSettingsUpdatedWsEvent event) {
        handleLobbyTimeLimitSettingsUpdate(event.getLobbyTimeLimitUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyPlayerStatusUpdatedWsEvent(LobbyPlayerStatusUpdatedWsEvent event) {
        handleLobbyPlayerStatusUpdate(event.getLobbyPlayerStatusUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyUpdatePlayerJoinedWsEvent(LobbyUpdatePlayerJoinedWsEvent event) {
        handleLobbyPlayerJoinedUpdate(event.getLobbyPlayerJoinedUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyUpdatePlayerLeftWsEvent(LobbyUpdatePlayerLeftWsEvent event) {
        handleLobbyPlayerLeftUpdate(event.getLobbyPlayerLeftUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyUpdatePostGameCreationWsEvent(LobbyUpdatePostGameCreationWsEvent event) {
        handleLobbyGameCreatedUpdate(event.getLobbyGameCreationUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyChatMessageSentWsEvent(LobbyChatMessageSentWsEvent event) {
        handleLobbyChatMessageSend(event.getLobbyChatMessageDto());
    }

    // Synchronised event handler used to ensure player leaving events are handled before a transaction commits, so that errors are not raised due to player being removed
    @EventListener
    void handleLobbyChatMessagePlayerLeftWsEvent(LobbyChatInfoMessageSentPreCommitWsEvent event) {
        handleLobbyChatMessageSend(event.getLobbyChatMessageDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLobbyGameEndedEvent(LobbyGameEndedEvent event) {
        handleLobbyGameEnded(event);
    }

}
