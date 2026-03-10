package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyChatInfoMessageSentPreCommitWsEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyChatMessageSentWsEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyUpdateWsEvent;
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

    // Emmit event notification when a lobby update occurs
    public void handleLobbyUpdate(LobbyDto lobbyDto) {
        // Create a message header detailing what type of update this is so that the frontend can respond accordingly
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_UPDATED",
                "payload", lobbyDto
        );

        String topic = "/topic/lobby/" + lobbyDto.getId();

        // Send updated Dto over websocket to the lobby topic where all active players are subscribed to
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    // Emmit event notification when a new message is sent in a lobby
    public void handleLobbyChatMessageSend(LobbyChatMessageDto lobbyChatMessageDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_CHAT_MESSAGE",
                "chatMessage", lobbyChatMessageDto
        );
        String topic = "/topic/lobby/" + lobbyChatMessageDto.getLobbyId();
        // Send message over websocket to lobby topic where all active players are subscribed to
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    // Emmit event notification when a new game starts in a lobby
    public void handleLobbyGameStart(GameDto gameDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_CREATED",
                "payload", gameDto
        );

        String topic = "/topic/lobby/" + gameDto.getLobbyId();

        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    // Used for listening to lobby update events sent from outside of lobby service layer e.g. game service
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLobbyUpdateWsEvent(LobbyUpdateWsEvent event) {
        handleLobbyUpdate(event.getLobbyDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLobbyChatMessageSentWsEvent(LobbyChatMessageSentWsEvent event) {
        handleLobbyChatMessageSend(event.getLobbyChatMessageDto());
    }

    // Synchronised event handler used to ensure player leaving events are handled before a transaction commits, so that errors are not raised due to player being removed
    @EventListener
    public void handleLobbyChatMessagePlayerLeftWsEvent(LobbyChatInfoMessageSentPreCommitWsEvent event) {
        handleLobbyChatMessageSend(event.getLobbyChatMessageDto());
    }

}
