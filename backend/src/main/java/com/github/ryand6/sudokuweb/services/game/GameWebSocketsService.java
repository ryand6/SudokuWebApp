package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.CellAcceptedPublicUpdate;
import com.github.ryand6.sudokuweb.domain.game.CellRejectedPublicUpdate;
import com.github.ryand6.sudokuweb.dto.entity.game.GameChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.game.GameEventDto;
import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerDto;
import com.github.ryand6.sudokuweb.dto.events.PlayerHighlightedCellDto;
import com.github.ryand6.sudokuweb.enums.GameResult;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import com.github.ryand6.sudokuweb.events.types.game.*;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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

    public void handleCellRejectedPublicUpdate(Long gameId, CellRejectedPublicUpdate cellRejectedPublicUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "PLAYER_CELL_UPDATE_REJECTED",
                "payload", cellRejectedPublicUpdate
        );
        String topic = "/topic/game/" + gameId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleCellAcceptedPublicUpdate(Long gameId, CellAcceptedPublicUpdate cellAcceptedPublicUpdate) {
        Map<String, Object> messageHeader = Map.of(
                "type", "PLAYER_CELL_UPDATE_ACCEPTED",
                "payload", cellAcceptedPublicUpdate
        );
        String topic = "/topic/game/" + gameId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleGameChatMessageSend(GameChatMessageDto gameChatMessageDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_CHAT_MESSAGE",
                "payload", gameChatMessageDto
        );
        String topic = "/topic/game/" + gameChatMessageDto.getGameId();
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleGamePlayerForfeit(Long gameId, GamePlayerDto gamePlayerDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "PLAYER_FORFEIT",
                "payload", gamePlayerDto
        );
        String topic = "/topic/game/" + gameId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handlePlayerFinishedGame(Long gameId, GamePlayerDto gamePlayerDto) {
        Map<String, Object> messageHeader = Map.of(
                "type", "PLAYER_FINISHED",
                "payload", gamePlayerDto
        );
        String topic = "/topic/game/" + gameId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleGameEndedPrematurely(Long gameId) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_ENDED_PREMATURELY"
        );
        String topic = "/topic/game/" + gameId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleGameResults(Long gameId, Map<Long, GameResult> gameResults) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_RESULTS_DETERMINED",
                "payload", gameResults
        );
        String topic = "/topic/game/" + gameId;
        simpMessagingTemplate.convertAndSend(topic, messageHeader);
    }

    public void handleGameStatusUpdate(Long gameId, GameStatus gameStatus) {
        Map<String, Object> messageHeader = Map.of(
                "type", "GAME_RESULTS_DETERMINED",
                "payload", gameStatus
        );
        String topic = "/topic/game/" + gameId;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCellRejectedPublicUpdateEvent(CellRejectedPublicUpdateEvent event) {
        handleCellRejectedPublicUpdate(event.getGameId(), event.getCellRejectedPublicUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleCellAcceptedPublicUpdateEvent(CellAcceptedPublicUpdateEvent event) {
        handleCellAcceptedPublicUpdate(event.getGameId(), event.getCellAcceptedPublicUpdate());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleGameChatMessageSentWsEvent(GameChatMessageSentWsEvent event) {
        handleGameChatMessageSend(event.getGameChatMessageDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleGamePlayerForfeitEvent(GamePlayerForfeitEvent event) {
        handleGamePlayerForfeit(event.getGameId(), event.getGamePlayerDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handlePlayerFinishedGameEvent(PlayerFinishedGameEvent event) {
        handlePlayerFinishedGame(event.getGameId(), event.getGamePlayerDto());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleGameEndedPrematurelyEvent(GameEndedPrematurelyEvent event) {
        handleGameEndedPrematurely(event.getGameId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleGameResultsEvent(GameResultsDeterminedEvent event) {
        handleGameResults(event.getGameId(), event.getGameResults());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleGameStatusUpdateEvent(GameStatusUpdateEvent event) {
        handleGameStatusUpdate(event.getGameId(), event.getGameStatus());
    }

}
