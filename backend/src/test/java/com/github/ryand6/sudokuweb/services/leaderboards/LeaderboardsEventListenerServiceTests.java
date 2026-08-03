package com.github.ryand6.sudokuweb.services.leaderboards;

import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameResult;
import com.github.ryand6.sudokuweb.events.types.leaderboards.LeaderboardUpdateEvent;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LeaderboardsEventListenerServiceTests {

    @Mock
    LeaderboardsService leaderboardsService;
    @InjectMocks
    LeaderboardsEventListenerService listener;

    @Test
    void handleLeaderboardUpdateEvent_win_callsRecordWin() {
        LeaderboardUpdateEvent event = new LeaderboardUpdateEvent(GameResult.WIN, GameMode.CLASSIC, 1L, 500);

        listener.handleLeaderboardUpdateEvent(event);

        verify(leaderboardsService).recordWin(GameMode.CLASSIC, 1L, 500);
    }

    @Test
    void handleLeaderboardUpdateEvent_forfeit_callsRecordLossWithZeroScore() {
        LeaderboardUpdateEvent event = new LeaderboardUpdateEvent(GameResult.FORFEIT, GameMode.CLASSIC, 1L, 999);

        listener.handleLeaderboardUpdateEvent(event);

        verify(leaderboardsService).recordLoss(GameMode.CLASSIC, 1L, 0);
    }
}
