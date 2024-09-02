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

    public static SudokuPuzzle createTestSudokuPuzzleA() {
        return SudokuPuzzle.builder().
                id(1L).
                initialBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                solution("892356741657148293143297586735682419986714325421935678374861952219573864568429137").
                difficulty("easy").
                build();
    }

    public static SudokuPuzzle createTestSudokuPuzzleB() {
        return SudokuPuzzle.builder().
                id(2L).
                initialBoardState("070004000000006900000329000007008010680932075090400600000295000002100000000800020").
                solution("973584261428716953516329847247658319681932475395471682764295138832167594159843726").
                difficulty("extreme").
                build();
    }

    public static SudokuPuzzle createTestSudokuPuzzleC() {
        return SudokuPuzzle.builder().
                id(3L).
                initialBoardState("050000021630020800020937005000095000902704603000260000200853040005040086840000030").
                solution("759486321634521897128937465416395278982714653573268914267853149395142786841679532").
                difficulty("medium").
                build();
    }

    public static Lobby createTestLobbyA() {
        return Lobby.builder().
                id(1L).
                lobbyName("Guru Lobby").
                isActive(true).
                build();
    }

    public static Lobby createTestLobbyB() {
        return Lobby.builder().
                id(2L).
                lobbyName("SudokuSquad").
                isActive(true).
                build();
    }

    public static Lobby createTestLobbyC() {
        return Lobby.builder().
                id(3L).
                lobbyName("Active").
                isActive(false).
                build();
    }

    public static LobbyState createTestLobbyStateA() {
        return LobbyState.builder().
                id(1L).
                lobbyId(1L).
                userId(1L).
                puzzleId(1L).
                currentBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                score(0).
                build();
    }

    public static LobbyState createTestLobbyStateB() {
        return LobbyState.builder().
                id(2L).
                lobbyId(2L).
                userId(2L).
                puzzleId(2L).
                currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020").
                score(10).
                build();
    }

    public static LobbyState createTestLobbyStateC() {
        return LobbyState.builder().
                id(3L).
                lobbyId(3L).
                userId(3L).
                puzzleId(3L).
                currentBoardState("750000021630020800020937005000095000902704603000260000200853040005040086840000030").
                score(5).
                build();
    }

}
