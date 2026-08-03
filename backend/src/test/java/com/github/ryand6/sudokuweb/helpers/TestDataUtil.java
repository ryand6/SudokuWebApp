package com.github.ryand6.sudokuweb.helpers;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerId;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsEntity;
import com.github.ryand6.sudokuweb.domain.lobby.*;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerId;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;
import com.github.ryand6.sudokuweb.enums.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public final class TestDataUtil {

    private TestDataUtil() {

    }

    public static UserEntity createTestUserA() {
        return UserEntity.builder().
                username("Henry").
                build();
    }

    public static UserEntity createTestUserB() {
        return UserEntity.builder().
                username("dk0ng").
                build();
    }

    public static UserEntity createTestUserC() {
        return UserEntity.builder().
                username("parf").
                build();
    }

    public static UserSettingsEntity createUserSettingsA(final UserEntity user) {
        return UserSettingsEntity.builder().
                userEntity(user).
                theme(Theme.DEFAULT).
                build();
    }

    public static SudokuPuzzleEntity createTestSudokuPuzzleA() {
        return SudokuPuzzleEntity.builder().
                initialBoardState(".923.6..1..7..8..3.432.7.8..3568.....8.....2.....3567..7.8.195.2..5..8..5..4.913.").
                solution("892356741657148293143297586735682419986714325421935678374861952219573864568429137").
                difficulty(Difficulty.EASY).
                build();
    }

    public static SudokuPuzzleEntity createTestSudokuPuzzleB() {
        return SudokuPuzzleEntity.builder().
                initialBoardState(".7...4........69.....329.....7..8.1.68.932.75.9.4..6.....295.....21........8...2.").
                solution("973584261428716953516329847247658319681932475395471682764295138832167594159843726").
                difficulty(Difficulty.EXTREME).
                build();
    }

    public static SudokuPuzzleEntity createTestSudokuPuzzleC() {
        return SudokuPuzzleEntity.builder().
                initialBoardState(".5.....2163..2.8...2.937..5....95...9.27.46.3...26....2..853.4...5.4..8684.....3.").
                solution("759486321634521897128937465416395278982714653573268914267853149395142786841679532").
                difficulty(Difficulty.MEDIUM).
                build();
    }

    public static LobbySettingsEntity createLobbySettingsA(LobbyEntity lobby) {
        return LobbySettingsEntity.builder()
                .difficulty(Difficulty.EASY)
                .timeLimit(TimeLimitPreset.QUICK)
                .isPublic(true)
                .lobbyEntity(lobby)
                .build();
    }

    public static LobbySettingsEntity createLobbySettingsB(LobbyEntity lobby) {
        return LobbySettingsEntity.builder()
                .difficulty(Difficulty.EASY)
                .timeLimit(TimeLimitPreset.QUICK)
                .isPublic(false)
                .lobbyEntity(lobby)
                .build();
    }

    private static LobbyCountdownEntity createLobbyCountdownA(LobbyEntity lobby) {
        return LobbyCountdownEntity.builder()
                .countdownActive(false)
                .lobbyEntity(lobby)
                .build();
    }

    public static LobbyEntity createTestLobbyA(final UserEntity userEntity, Set<LobbyPlayerEntity> players) {
        LobbyEntity lobby = LobbyEntity.builder().
                lobbyName("Guru Lobby").
                isActive(true).
                inGame(true).
                lobbyPlayers(players).
                host(userEntity).build();

        lobby.setLobbySettingsEntity(createLobbySettingsA(lobby));
        lobby.setLobbyCountdownEntity(createLobbyCountdownA(lobby));
        return lobby;
    }

    public static LobbyEntity createTestLobbyB(final UserEntity userEntity, Set<LobbyPlayerEntity> players) {
        LobbyEntity lobby =  LobbyEntity.builder().
                lobbyName("SudokuSquad").
                isActive(true).
                inGame(true).
                lobbyPlayers(players).
                host(userEntity).
                build();

        lobby.setLobbySettingsEntity(createLobbySettingsB(lobby));
        lobby.setLobbyCountdownEntity(createLobbyCountdownA(lobby));
        return lobby;
    }

    public static LobbyEntity createTestLobbyC(final UserEntity userEntity, Set<LobbyPlayerEntity> players) {
        LobbyEntity lobby = LobbyEntity.builder().
                lobbyName("In Game Lobby").
                isActive(true).
                inGame(true).
                lobbyPlayers(players).
                host(userEntity).
                build();

        lobby.setLobbySettingsEntity(createLobbySettingsA(lobby));
        lobby.setLobbyCountdownEntity(createLobbyCountdownA(lobby));
        return lobby;
    }

    public static GamePlayerEntity createGamePlayerA(final UserEntity userEntity, final GameEntity gameEntity) {
        return GamePlayerEntity.builder()
                .id(new GamePlayerId(gameEntity.getId(), userEntity.getId()))
                .userEntity(userEntity)
                .gameEntity(gameEntity)
                .score(1500)
                .firsts(8)
                .mistakes(2)
                .maxStreak(6)
                .playerColour(PlayerColour.ONE)
                .gameResult(GameResult.PENDING)
                .finishedGame(true)
                .build();
    }

    public static GamePlayerEntity createGamePlayerB(final UserEntity userEntity, final GameEntity gameEntity) {
        return GamePlayerEntity.builder()
                .id(new GamePlayerId(gameEntity.getId(), userEntity.getId()))
                .userEntity(userEntity)
                .gameEntity(gameEntity)
                .score(900)
                .firsts(2)
                .mistakes(5)
                .maxStreak(2)
                .playerColour(PlayerColour.TWO)
                .gameResult(GameResult.PENDING)
                .finishedGame(false)
                .build();
    }

    public static GamePlayerStateEntity createTestGameStateA(final GamePlayerEntity gamePlayer, final UserEntity userEntity) {
        return GamePlayerStateEntity.builder().
                id(new GamePlayerId(gamePlayer.getGameEntity().getId(), userEntity.getId())).
                notes(new byte[81 * 2]).
                consecutiveMistakeCount(0).
                currentStreak(0).
                activeMultiplier(0).
                gamePlayerEntity(gamePlayer).
                currentBoardState(".923.6..1..7..8..3.432.7.8..3568.....8.....2.....3567..7.8.195.2..5..8..5..4.913.").
                build();
    }

    public static GamePlayerStateEntity createTestGameStateB(final GamePlayerEntity gamePlayer, final UserEntity userEntity) {
        return GamePlayerStateEntity.builder().
                id(new GamePlayerId(gamePlayer.getGameEntity().getId(), userEntity.getId())).
                notes(new byte[81 * 2]).
                consecutiveMistakeCount(0).
                currentStreak(0).
                activeMultiplier(0).
                gamePlayerEntity(gamePlayer).
                currentBoardState("973..4........69.....329.....7..8.1.68.932.75.9.4..6.....295.....21........8...2.").
                build();
    }

    public static GamePlayerStateEntity createTestGameStateC(final GamePlayerEntity gamePlayer, final UserEntity userEntity) {
        return GamePlayerStateEntity.builder().
                id(new GamePlayerId(gamePlayer.getGameEntity().getId(), userEntity.getId())).
                notes(new byte[81 * 2]).
                consecutiveMistakeCount(0).
                currentStreak(0).
                activeMultiplier(0).
                gamePlayerEntity(gamePlayer).
                currentBoardState("75.....2163..2.8...2.937..5....95...9.27.46.3...26....2..853.4...5.4..8684.....3.").
                build();
    }

    public static GameEntity createGame(final LobbyEntity lobbyEntity, final SudokuPuzzleEntity sudokuPuzzleEntity) {
        return GameEntity.builder()
                .lobbyEntity(lobbyEntity)
                .sudokuPuzzleEntity(sudokuPuzzleEntity)
                .gameStatus(GameStatus.IN_PROGRESS)
                .gameStartsAt(Instant.now().minusSeconds(120))
                .gameEndsAt(Instant.now().plusSeconds(60))
                .gamePlayerEntities(new HashSet<>())
                .build();
    }

    public static LobbyPlayerEntity createTestLobbyPlayer(final LobbyEntity lobbyEntity, final UserEntity userEntity) {
        return LobbyPlayerEntity.builder()
                .id(new LobbyPlayerId(lobbyEntity.getId(), userEntity.getId()))
                .lobby(lobbyEntity)
                .user(userEntity)
                .build();
    }

    public static GameSettingsEntity createGameSettingsA(final GameEntity game) {
        return GameSettingsEntity.builder().
                gameEntity(game)
                .gameMode(GameMode.CLASSIC)
                .gameType(GameType.RANKED)
                .build();
    }

}
