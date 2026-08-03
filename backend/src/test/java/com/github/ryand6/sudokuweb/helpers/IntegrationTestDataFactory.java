package com.github.ryand6.sudokuweb.helpers;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.GameRepository;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerId;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerRepository;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateRepository;
import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsEntity;
import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsRepository;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.domain.user.UserRepository;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsRepository;
import com.github.ryand6.sudokuweb.enums.GameResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class IntegrationTestDataFactory {

    @Autowired UserRepository userRepository;
    @Autowired
    UserSettingsRepository userSettingsRepository;
    @Autowired LobbyRepository lobbyRepository;
    @Autowired LobbyPlayerRepository lobbyPlayerRepository;
    @Autowired SudokuPuzzleRepository sudokuPuzzleRepository;
    @Autowired GameRepository gameRepository;
    @Autowired GamePlayerRepository gamePlayerRepository;
    @Autowired GamePlayerStateRepository gamePlayerStateRepository;
    @Autowired GameSettingsRepository gameSettingsRepository;

    public record TestGameGraph(GameEntity game, Long player1Id, Long player2Id) {}

    public TestGameGraph createInProgressRankedGameWithTwoPlayers() {
        UserEntity hostUser = userRepository.save(TestDataUtil.createTestUserA());
        UserEntity opponent = userRepository.save(TestDataUtil.createTestUserB());

        UserSettingsEntity hostSettings = TestDataUtil.createUserSettingsA(hostUser);
        UserSettingsEntity opponentSettings = TestDataUtil.createUserSettingsA(opponent);
        userSettingsRepository.save(hostSettings);
        userSettingsRepository.save(opponentSettings);

        hostUser.setUserSettingsEntity(hostSettings);
        opponent.setUserSettingsEntity(opponentSettings);

        userRepository.save(hostUser);
        userRepository.save(opponent);

        LobbyEntity lobby = TestDataUtil.createTestLobbyA(hostUser, new HashSet<>());
        lobby = lobbyRepository.save(lobby);

        LobbyPlayerEntity hostPlayer = lobbyPlayerRepository.save(
                TestDataUtil.createTestLobbyPlayer(lobby, hostUser));
        LobbyPlayerEntity opponentPlayer = lobbyPlayerRepository.save(
                TestDataUtil.createTestLobbyPlayer(lobby, opponent));
        Set<LobbyPlayerEntity> lobbyPlayers = new HashSet<>(Set.of(hostPlayer, opponentPlayer));
        lobby.setLobbyPlayers(lobbyPlayers);
        lobby = lobbyRepository.save(lobby);

        SudokuPuzzleEntity puzzle = sudokuPuzzleRepository.save(TestDataUtil.createTestSudokuPuzzleA());

        GameEntity game = TestDataUtil.createGame(lobby, puzzle);
        game = gameRepository.save(game);

        lobby.setCurrentGameId(game.getId());
        lobbyRepository.save(lobby);

        GameSettingsEntity settings = TestDataUtil.createGameSettingsA(game);
        settings = gameSettingsRepository.save(settings);
        game.setGameSettingsEntity(settings);

        GamePlayerEntity player1 = TestDataUtil.createGamePlayerA(hostUser, game);
        player1.setId(new GamePlayerId());
        player1.setGameResult(GameResult.PENDING);
        player1.setLeaderboardScore(1000);
        player1 = gamePlayerRepository.save(player1);

        GamePlayerEntity player2 = TestDataUtil.createGamePlayerB(opponent, game);
        player2.setId(new GamePlayerId());
        player2.setGameResult(GameResult.PENDING);
        player2 = gamePlayerRepository.save(player2);

        GamePlayerStateEntity player1State = TestDataUtil.createTestGameStateA(player1, hostUser);
        player1State = gamePlayerStateRepository.save(player1State);
        player1.setGamePlayerStateEntity(player1State);
        player1 = gamePlayerRepository.save(player1);

        GamePlayerStateEntity player2State = TestDataUtil.createTestGameStateB(player2, opponent);
        player2State = gamePlayerStateRepository.save(player2State);
        player2.setGamePlayerStateEntity(player2State);
        player2 = gamePlayerRepository.save(player2);

        Set<GamePlayerEntity> players = new HashSet<>(Set.of(player1, player2));
        game.setGamePlayerEntities(players);
        game = gameRepository.save(game);

        return new TestGameGraph(game, hostUser.getId(), opponent.getId());
    }
}
