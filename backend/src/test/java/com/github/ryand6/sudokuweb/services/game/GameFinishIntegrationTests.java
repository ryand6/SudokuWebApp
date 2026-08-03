package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.GameRepository;
import com.github.ryand6.sudokuweb.domain.leaderboards.LeaderboardsRepository;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import com.github.ryand6.sudokuweb.events.types.game.FinishGameEvent;
import com.github.ryand6.sudokuweb.helpers.IntegrationTestDataFactory;
import com.github.ryand6.sudokuweb.helpers.TestDataUtil;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.Commit;
import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class GameFinishIntegrationTests extends AbstractIntegrationTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    LeaderboardsRepository leaderboardsRepository;
    @Autowired
    IntegrationTestDataFactory testDataFactory;

    Long gameId;
    GameEntity game;
    Long player1Id;
    Long player2Id;

    @BeforeEach
    void setUp() {
        var graph = testDataFactory.createInProgressRankedGameWithTwoPlayers();
        gameId = graph.game().getId();
        player1Id = graph.player1Id();
        player2Id = graph.player2Id();
    }

    @Test
    @Transactional
    @Commit // required so AFTER_COMMIT listeners actually fire
    void finishGameEvent_marksGameFinishedAndPersistsLeaderboardForBothPlayers() {
        eventPublisher.publishEvent(new FinishGameEvent(gameId));

        GameEntity updated = gameRepository.findById(gameId).orElseThrow();
        assertThat(updated.getGameStatus()).isEqualTo(GameStatus.FINISHED);

        assertThat(leaderboardsRepository.findByGameModeAndUserEntity_Id(GameMode.CLASSIC, player1Id)).isPresent();
        assertThat(leaderboardsRepository.findByGameModeAndUserEntity_Id(GameMode.CLASSIC, player2Id)).isPresent();
    }
}
