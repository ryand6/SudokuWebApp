package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.dto.entity.game.GameEventDto;
import com.github.ryand6.sudokuweb.dto.events.PlayerHighlightedCellDto;
import com.github.ryand6.sudokuweb.events.types.game.GameLogSendEvent;
import com.github.ryand6.sudokuweb.events.types.game.PlayerHighlightedCellUpdateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GameWebSocketsService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameWebSocketsService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void handlePlayerHighlightedCellUpdate(PlayerHighlightedCellDto playerHighlightedCellDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "HIGHLIGHTED_CELL_UPDATE",
                "payload", playerHighlightedCellDto
        );
        String topic = "/topic/game/" + playerHighlightedCellDto.getGameId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleGameLogSend(GameEventDto gameEventDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_EVENT",
                "payload", gameEventDto
        );
        String topic = "/topic/game/" + gameEventDto.getGameId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    @EventListener
    void handlePlayerHighlightedCellUpdateEvent(PlayerHighlightedCellUpdateEvent event) {
        handlePlayerHighlightedCellUpdate(event.getPlayerHighlightedCellDto());
    }

    @EventListener
    void handleGameLogSendEvent(GameLogSendEvent gameLogSendEvent) {
        handleGameLogSend(gameLogSendEvent.getGameEventDto());
    }

}
