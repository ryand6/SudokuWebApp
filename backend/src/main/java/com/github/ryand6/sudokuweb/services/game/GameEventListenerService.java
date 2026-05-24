package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.events.types.game.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class GameEventListenerService {

    private final GameService gameService;
    private final GameEventService gameEventService;
    private final GameInMemoryStateService gameInMemoryStateService;

    public GameEventListenerService(GameService gameService,
                                    GameEventService gameEventService,
                                    GameInMemoryStateService gameInMemoryStateService) {
        this.gameService = gameService;
        this.gameEventService = gameEventService;
        this.gameInMemoryStateService = gameInMemoryStateService;
    }

    //#######################//
    // Game Service          //
    //#######################//

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handlePlayerFinishEvent(HandlePlayerFinishEvent event) {
        gameService.handlePlayerFinish(event.getGameId(), event.getUserId());
    }

    @EventListener
    @Transactional
    void handleFinishGameEvent(FinishGameEvent event) {
        gameService.markAllPlayersFinished(event.getGameId());
    }

    @EventListener
    @Transactional
    void handleCloseGameEvent(CloseGameEvent event) {
        gameService.closeGame(event.getGameId());
    }

    @EventListener
    @Transactional
    void handleStartGameEvent(StartGameEvent event) {
        gameService.startGame(event.getGameId());
    }

    //#######################//
    // Game Event Service    //
    //#######################//

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void handleCreateGameEvent(CreateGameLogEvent event) {
        gameEventService.createGameEvent(event.getGameId(), event.getUserId(), event.getGameEventRequest());
    }

    //#######################//
    // Game In-Memory Service//
    //#######################//

    @EventListener
    void handleGamePlayerLeftInMemoryStateEvent(GamePlayerLeftEvent event) {
        gameInMemoryStateService.removeGamePlayer(event.getGameId(), event.getUserId());
    }

    @EventListener
    void handleGameLeftInMemoryStateEvent(GameClosedMembershipUpdateEvent event) {
        gameInMemoryStateService.removeGame(event.getGameId());
    }


}
