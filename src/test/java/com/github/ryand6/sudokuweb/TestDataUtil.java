package com.github.ryand6.sudokuweb;

import com.github.ryand6.sudokuweb.domain.*;

public final class TestDataUtil {

    private TestDataUtil() {

    }

    public static User createTestUserA() {
        return User.builder().
                id(1L).
                username("Henry").
                passwordHash("a4ceE42GHa").
                build();
    }

    public static User createTestUserB() {
        return User.builder().
                id(2L).
                username("dk0ng").
                passwordHash("34hEA3cbe").
                build();
    }

    public static User createTestUserC() {
        return User.builder().
                id(3L).
                username("parf").
                passwordHash("dA5cfB12c").
                build();
    }

    public static Score createTestScoreA() {
        return Score.builder().
                id(1L).
                userId(1L).
                totalScore(150).
                gamesPlayed(1).
                build();
    }

    public static Score createTestScoreB() {
        return Score.builder().
                id(2L).
                userId(2L).
                totalScore(450).
                gamesPlayed(5).
                build();
    }

    public static Score createTestScoreC() {
        return Score.builder().
                id(3L).
                userId(3L).
                totalScore(50).
                gamesPlayed(2).
                build();
    }

    public static SudokuPuzzle createTestSudokuPuzzle() {
        return SudokuPuzzle.builder().
                id(1L).
                initialBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                solution("892356741657148293143297586735682419986714325421935678374861952219573864568429137").
                difficulty("easy").
                build();
    }

    public static Lobby createTestLobby() {
        return Lobby.builder().
                id(1L).
                lobbyName("Guru Lobby").
                isActive(true).
                build();
    }

    public static LobbyState createTestLobbyState() {
        return LobbyState.builder().
                id(1L).
                lobbyId(1L).
                userId(1L).
                puzzleId(1L).
                currentBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                score(0).
                build();
    }

}
