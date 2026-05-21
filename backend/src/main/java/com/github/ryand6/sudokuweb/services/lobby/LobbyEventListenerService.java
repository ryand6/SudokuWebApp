package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.events.types.game.GameClosedEvent;
import com.github.ryand6.sudokuweb.events.types.game.GamePlayerLeftEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.EndLobbyPlayerInGameStatusEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyCountdownResetEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.UpdateLobbyCountdownSchedulerEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class LobbyEventListenerService {

    private final LobbyService lobbyService;
    private final LobbyCountdownMutationService lobbyCountdownMutationService;
    private final LobbyCountdownSchedulerService lobbyCountdownSchedulerService;
    private final LobbyPlayerService lobbyPlayerService;

    public LobbyEventListenerService(LobbyService lobbyService,
                                     LobbyCountdownMutationService lobbyCountdownMutationService,
                                     LobbyCountdownSchedulerService lobbyCountdownSchedulerService,
                                     LobbyPlayerService lobbyPlayerService) {
        this.lobbyService = lobbyService;
        this.lobbyCountdownMutationService = lobbyCountdownMutationService;
        this.lobbyCountdownSchedulerService = lobbyCountdownSchedulerService;
        this.lobbyPlayerService = lobbyPlayerService;
    }

    //#######################//
    // Lobby Service         //
    //#######################//

    @EventListener
    void handleGamePlayerLeftLobbyEvent(GamePlayerLeftEvent event) {
        lobbyService.removeFromLobby(event.getLobbyId(), event.getUserId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleGameClosedEvent(GameClosedEvent event) {
        lobbyService.handleGameFinish(event.getLobbyId());
    }

    //##################################//
    // Lobby Countdown Mutation Service //
    //##################################//

    @EventListener
    void handleLobbyCountdownResetEvent(LobbyCountdownResetEvent event) {
        lobbyCountdownMutationService.safeCountdownReset(event.getLobbyCountdown());
    }

    //###################################//
    // Lobby Countdown Scheduler Service //
    //###################################//

    @EventListener
    void handleUpdateLobbyCountdownSchedulerEvent(UpdateLobbyCountdownSchedulerEvent event) {
        lobbyCountdownSchedulerService.handleCountdownEvaluationResult(event.getLobbyId(), event.getCountdownEvaluationResult());
    }

    //#######################//
    // Lobby Player Service  //
    //#######################//

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleEndLobbyPlayerInGameStatusEvent(EndLobbyPlayerInGameStatusEvent event) {
        lobbyPlayerService.revertAllLobbyPlayerInGameStatuses(event.getLobbyId());
    }

}
