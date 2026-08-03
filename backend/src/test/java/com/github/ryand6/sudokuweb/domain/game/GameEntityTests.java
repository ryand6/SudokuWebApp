package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import com.github.ryand6.sudokuweb.exceptions.game.IllegalGameStatusChangeException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class GameEntityTests {

    @Test
    void isGameFinished_trueWhenAllNonForfeitedPlayersFinished() {
        GamePlayerEntity p1 = mock(GamePlayerEntity.class);
        GamePlayerEntity p2 = mock(GamePlayerEntity.class);
        when(p1.getGameResult()).thenReturn(null);
        when(p2.getGameResult()).thenReturn(null);
        when(p1.isFinishedGame()).thenReturn(true);
        when(p2.isFinishedGame()).thenReturn(true);

        GameEntity game = GameEntity.builder()
                .gamePlayerEntities(Set.of(p1, p2))
                .build();

        assertThat(game.isGameFinished()).isTrue();
    }

    @Test
    void isGameFinished_falseWhenAPlayerStillPlaying() {
        GamePlayerEntity p1 = mock(GamePlayerEntity.class);
        GamePlayerEntity p2 = mock(GamePlayerEntity.class);
        when(p1.getGameResult()).thenReturn(null);
        when(p2.getGameResult()).thenReturn(null);
        when(p1.isFinishedGame()).thenReturn(true);
        when(p2.isFinishedGame()).thenReturn(false);

        GameEntity game = GameEntity.builder()
                .gamePlayerEntities(Set.of(p1, p2))
                .build();

        assertThat(game.isGameFinished()).isFalse();
    }

    @Test
    void finishGame_throwsIfNotInProgress() {
        GameEntity game = GameEntity.builder().gameStatus(GameStatus.COUNTDOWN).build();
        assertThrows(IllegalGameStatusChangeException.class, game::finishGame);
    }

    @Test
    void finishGame_setsFinishedStatusAndTimestampWhenInProgress() {
        GameEntity game = GameEntity.builder().gameStatus(GameStatus.IN_PROGRESS).build();
        game.finishGame();
        assertThat(game.getGameStatus()).isEqualTo(GameStatus.FINISHED);
        assertThat(game.getGameEndedAt()).isNotNull();
    }

    @Test
    void closeGame_throwsIfNotFinished() {
        GameEntity game = GameEntity.builder().gameStatus(GameStatus.IN_PROGRESS).build();
        assertThrows(IllegalGameStatusChangeException.class, game::closeGame);
    }

    @Test
    void closeGame_setsClosedStatusWhenFinished() {
        GameEntity game = GameEntity.builder().gameStatus(GameStatus.FINISHED).build();
        game.closeGame();
        assertThat(game.getGameStatus()).isEqualTo(GameStatus.CLOSED);
    }
}
