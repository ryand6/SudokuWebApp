package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LobbyWebSocketsService {

    public void handleLobbyUpdate(LobbyDto lobbyDto, SimpMessagingTemplate messagingTemplate) {
        // Create a message header detailing what type of update this is so that the frontend can respond accordingly
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_UPDATED",
                "payload", lobbyDto
        );

        String topic = "/topic/lobby/" + lobbyDto.getId();

        // Send updated Dto over websocket to the lobby topic where all active players are subscribed to
        messagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleLobbyChatMessage(Long lobbyId, String username, String message, SimpMessagingTemplate messagingTemplate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "LOBBY_CHAT_MESSAGE",
                "username", username,
                "payload", message
        );

        String topic = "/topic/lobby/" + lobbyId;

        // Send message over websocket to lobby topic where all active players are subscribed to
        messagingTemplate.convertAndSend(topic, messageHeader);
    }

}
