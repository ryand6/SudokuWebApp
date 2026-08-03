package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.events.types.game.CloseGameEvent;
import com.github.ryand6.sudokuweb.events.types.game.FinishGameEvent;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameEventListenerServiceTests {

    @Mock
    GameService gameService;
    @Mock
    GameEventService gameEventService;
    @Mock
    GameInMemoryStateService gameInMemoryStateService;

    @InjectMocks
    GameEventListenerService listener;

    @Test
    void handleFinishGameEvent_delegatesToMarkAllPlayersFinished() {
        FinishGameEvent event = new FinishGameEvent(1L);

        listener.handleFinishGameEvent(event);

        verify(gameService).markAllPlayersFinished(1L);
    }

    @Test
    void handleCloseGameEvent_delegatesToCloseGame() {
        CloseGameEvent event = new CloseGameEvent(1L);

        listener.handleCloseGameEvent(event);

        verify(gameService).closeGame(1L);
    }
}
