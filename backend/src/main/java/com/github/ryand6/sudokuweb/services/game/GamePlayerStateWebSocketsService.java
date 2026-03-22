package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.player.state.CellValueUpdate;
import com.github.ryand6.sudokuweb.events.types.game.CellUpdateSubmissionInvalidEvent;
import com.github.ryand6.sudokuweb.events.types.game.CellUpdateSubmissionRejectedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GamePlayerStateWebSocketsService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public GamePlayerStateWebSocketsService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void handleCellUpdateSubmissionInvalid(Long gameId, Long userId, CellValueUpdate cellValueUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "CELL_UPDATE_INVALID",
                "payload", cellValueUpdate
        );
        String topic = "/topic/game/" + gameId + "/user/" + userId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleCellUpdateSubmissionRejected(Long gameId, Long userId, CellValueUpdate cellValueUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "CELL_UPDATE_REJECTED",
                "payload", cellValueUpdate
        );
        String topic = "/topic/game/" + gameId + "/user/" + userId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    @EventListener
    void handleCellUpdateSubmissionInvalidEvent(CellUpdateSubmissionInvalidEvent event) {
        handleCellUpdateSubmissionInvalid(event.getGameId(), event.getUserId(), event.getCellValueUpdate());
    }

    @EventListener
    void handleCellUpdateSubmissionRejectedEvent(CellUpdateSubmissionRejectedEvent event) {
        handleCellUpdateSubmissionRejected(event.getGameId(), event.getUserId(), event.getCellValueUpdate());
    }

}
