package com.github.ryand6.sudokuweb;

import com.github.ryand6.sudokuweb.domain.*;

public final class TestDataUtil {

    private TestDataUtil() {

    }

    public static User createTestUser() {
        return User.builder().
                id(1L).
                username("Henry").
                passwordHash("a4ceE42GHa").
                build();
    }

    public static Score createTestScore() {
        return Score.builder().
                id(1L).
                userId(1L).
                totalScore(150).
                gamesPlayed(1).
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
