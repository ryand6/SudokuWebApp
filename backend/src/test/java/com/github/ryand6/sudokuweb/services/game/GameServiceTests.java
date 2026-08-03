package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.GameRepository;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventSequenceRepository;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerRepository;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateRepository;
import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.GameType;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GameEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GamePlayerEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.game.PrivateGamePlayerStateEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.services.puzzle.SudokuPuzzleService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTests {

    @Mock
    GameRepository gameRepository;
    @Mock
    SudokuPuzzleService sudokuPuzzleService;
    @Mock
    GameEntityDtoMapper gameEntityDtoMapper;
    @Mock
    GamePlayerEntityDtoMapper gamePlayerEntityDtoMapper;
    @Mock
    GamePlayerStateRepository gamePlayerStateRepository;
    @Mock
    GamePlayerRepository gamePlayerRepository;
    @Mock
    PrivateGamePlayerStateEntityDtoMapper privateGamePlayerStateEntityDtoMapper;
    @Mock
    LobbyRepository lobbyRepository;
    @Mock
    LobbyEntityDtoMapper lobbyEntityDtoMapper;
    @Mock
    GameEventSequenceRepository gameEventSequenceRepository;
    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    GameService gameService;

    @Test
    void markAllPlayersFinished_skipsAlreadyFinishedPlayers() {
        GamePlayerEntity finishedPlayer = mock(GamePlayerEntity.class);
        when(finishedPlayer.isFinishedGame()).thenReturn(true);

        GameEntity game = GameEntity.builder()
                .id(1L)
                .gamePlayerEntities(new HashSet<>(Set.of(finishedPlayer)))
                .build();

        when(gameRepository.findByIdWithLock(1L)).thenReturn(Optional.of(game));

        gameService.markAllPlayersFinished(1L);

        verify(finishedPlayer, never()).markGameFinished();
    }

    @Test
    void markAllPlayersFinished_finishesUnfinishedPlayers() {
        GamePlayerEntity unfinishedPlayer = mock(GamePlayerEntity.class);
        UserEntity user = mock(UserEntity.class);
        when(unfinishedPlayer.isFinishedGame()).thenReturn(false);
        when(unfinishedPlayer.getUserEntity()).thenReturn(user);
        when(user.getId()).thenReturn(2L);

        GameEntity game = GameEntity.builder()
                .id(1L)
                .gamePlayerEntities(new HashSet<>(Set.of(unfinishedPlayer)))
                .gameSettingsEntity(GameSettingsEntity.builder().gameType(GameType.CASUAL).build())
                .build();

        when(gameRepository.findByIdWithLock(1L)).thenReturn(Optional.of(game));
        when(gamePlayerRepository.findByCompositeId(1L, 2L)).thenReturn(Optional.of(unfinishedPlayer));

        gameService.markAllPlayersFinished(1L);

        verify(unfinishedPlayer).markGameFinished();
    }

    @Test
    void handlePlayerFinish_isNoOpIfAlreadyFinished() {
        GamePlayerEntity gamePlayer = mock(GamePlayerEntity.class);
        when(gamePlayer.isFinishedGame()).thenReturn(true);
        when(gamePlayerRepository.findByCompositeId(1L, 2L)).thenReturn(Optional.of(gamePlayer));

        gameService.handlePlayerFinish(1L, 2L);

        verify(gamePlayer, never()).markGameFinished();
        verifyNoInteractions(applicationEventPublisher);
    }
}
