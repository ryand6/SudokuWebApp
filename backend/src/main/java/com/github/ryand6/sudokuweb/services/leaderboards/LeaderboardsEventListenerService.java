package com.github.ryand6.sudokuweb.services.leaderboards;

import com.github.ryand6.sudokuweb.events.types.leaderboards.LeaderboardUpdateEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class LeaderboardsEventListenerService {

    private final LeaderboardsService leaderboardsService;

    public LeaderboardsEventListenerService(LeaderboardsService leaderboardsService) {
        this.leaderboardsService = leaderboardsService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handleLeaderboardUpdateEvent(LeaderboardUpdateEvent event) {

        System.out.println("\n\nLeaderboardUpdateEvent intercepted\n\n");

        switch (event.getGameResult()) {
            case WIN -> leaderboardsService.recordWin(event.getGameMode(), event.getUserId(), event.getLeaderboardScore());
            case LOSS -> leaderboardsService.recordLoss(event.getGameMode(), event.getUserId(), event.getLeaderboardScore());
            case DRAW -> leaderboardsService.recordDraw(event.getGameMode(), event.getUserId(), event.getLeaderboardScore());
            case FORFEIT -> leaderboardsService.recordLoss(event.getGameMode(), event.getUserId(), 0);
        }

        System.out.println("\n\nLeaderboardUpdateEvent handled\n\n");
    }

}
