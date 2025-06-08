package com.github.ryand6.sudokuweb;

import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.PlayerColour;

import java.util.HashSet;

public final class TestDataUtil {

    private TestDataUtil() {

    }

    public static UserEntity createTestUserA(final ScoreEntity scoreEntity) {
        return UserEntity.builder().
                scoreEntity(scoreEntity).
                username("Henry").
                provider("google").
                providerId("a4ceE42GHa").
                build();
    }

    public static UserEntity createTestUserB(final ScoreEntity scoreEntity) {
        return UserEntity.builder().
                scoreEntity(scoreEntity).
                username("dk0ng").
                provider("github").
                providerId("34hEA3cbe").
                build();
    }

    public static UserEntity createTestUserC(final ScoreEntity scoreEntity) {
        return UserEntity.builder().
                scoreEntity(scoreEntity).
                username("parf").
                provider("facebook").
                providerId("dA5cfB12c").
                build();
    }

    public static ScoreEntity createTestScoreA() {
        return ScoreEntity.builder().
                totalScore(150).
                gamesPlayed(1).
                build();
    }

    public static ScoreEntity createTestScoreB() {
        return ScoreEntity.builder().
                totalScore(450).
                gamesPlayed(5).
                build();
    }

    public static ScoreEntity createTestScoreC() {
        return ScoreEntity.builder().
                totalScore(50).
                gamesPlayed(2).
                build();
    }

    public static SudokuPuzzleEntity createTestSudokuPuzzleA() {
        return SudokuPuzzleEntity.builder().
                initialBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                solution("892356741657148293143297586735682419986714325421935678374861952219573864568429137").
                difficulty(Difficulty.EASY).
                build();
    }

    public static SudokuPuzzleEntity createTestSudokuPuzzleB() {
        return SudokuPuzzleEntity.builder().
                initialBoardState("070004000000006900000329000007008010680932075090400600000295000002100000000800020").
                solution("973584261428716953516329847247658319681932475395471682764295138832167594159843726").
                difficulty(Difficulty.EXTREME).
                build();
    }

    public static SudokuPuzzleEntity createTestSudokuPuzzleC() {
        return SudokuPuzzleEntity.builder().
                initialBoardState("050000021630020800020937005000095000902704603000260000200853040005040086840000030").
                solution("759486321634521897128937465416395278982714653573268914267853149395142786841679532").
                difficulty(Difficulty.MEDIUM).
                build();
    }

    public static LobbyEntity createTestLobbyA(UserEntity userEntity) {
        HashSet<UserEntity> set = new HashSet<>();
        set.add(userEntity);
        return LobbyEntity.builder().
                lobbyName("Guru Lobby").
                isActive(true).
                isPublic(true).
                inGame(false).
                userEntities(set).
                host(userEntity).
                build();
    }

    public static LobbyEntity createTestLobbyB(UserEntity userEntityA, UserEntity userEntityB) {
        HashSet<UserEntity> set = new HashSet<>();
        set.add(userEntityA);
        set.add(userEntityB);
        return LobbyEntity.builder().
                lobbyName("SudokuSquad").
                isActive(true).
                isPublic(false).
                inGame(false).
                joinCode("aey3g-yagy3i3-u3ygu34").
                userEntities(set).
                host(userEntityA).
                build();
    }

    public static LobbyEntity createTestLobbyC(UserEntity userEntityA, UserEntity userEntityB, UserEntity userEntityC) {
        HashSet<UserEntity> set = new HashSet<>();
        set.add(userEntityA);
        set.add(userEntityB);
        set.add(userEntityC);
        return LobbyEntity.builder().
                lobbyName("Active").
                isActive(false).
                isPublic(false).
                inGame(true).
                joinCode("eipioje-stretg4-2et44t").
                userEntities(set).
                host(userEntityA).
                build();
    }

    public static GameStateEntity createTestGameStateA(final GameEntity gameEntity, final UserEntity userEntity) {
        return GameStateEntity.builder().
                gameEntity(gameEntity).
                userEntity(userEntity).
                currentBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                score(0).
                playerColour(PlayerColour.BLUE).
                build();
    }

    public static GameStateEntity createTestGameStateB(final GameEntity gameEntity, final UserEntity userEntity) {
        return GameStateEntity.builder().
                gameEntity(gameEntity).
                userEntity(userEntity).
                currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020").
                score(10).
                playerColour(PlayerColour.GREEN).
                build();
    }

    public static GameStateEntity createTestGameStateC(final GameEntity gameEntity, final UserEntity userEntity) {
        return GameStateEntity.builder().
                gameEntity(gameEntity).
                userEntity(userEntity).
                currentBoardState("750000021630020800020937005000095000902704603000260000200853040005040086840000030").
                score(5).
                playerColour(PlayerColour.PURPLE).
                build();
    }

    public static GameEntity createTestGame(final LobbyEntity lobbyEntity, final SudokuPuzzleEntity sudokuPuzzleEntity) {
        return GameEntity.builder()
                .lobbyEntity(lobbyEntity)
                .sudokuPuzzleEntity(sudokuPuzzleEntity)
                .build();
    }

}
