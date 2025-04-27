package com.github.ryand6.sudokuweb;

import com.github.ryand6.sudokuweb.domain.*;

import java.util.HashSet;

public final class TestDataUtil {

    private TestDataUtil() {

    }

    public static User createTestUserA(final Score score) {
        return User.builder().
                score(score).
                username("Henry").
                passwordHash("a4ceE42GHa").
                build();
    }

    public static User createTestUserB(final Score score) {
        return User.builder().
                score(score).
                username("dk0ng").
                passwordHash("34hEA3cbe").
                build();
    }

    public static User createTestUserC(final Score score) {
        return User.builder().
                score(score).
                username("parf").
                passwordHash("dA5cfB12c").
                build();
    }

    public static Score createTestScoreA() {
        return Score.builder().
                totalScore(150).
                gamesPlayed(1).
                build();
    }

    public static Score createTestScoreB() {
        return Score.builder().
                totalScore(450).
                gamesPlayed(5).
                build();
    }

    public static Score createTestScoreC() {
        return Score.builder().
                totalScore(50).
                gamesPlayed(2).
                build();
    }

    public static SudokuPuzzle createTestSudokuPuzzleA() {
        return SudokuPuzzle.builder().
                initialBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                solution("892356741657148293143297586735682419986714325421935678374861952219573864568429137").
                difficulty(SudokuPuzzle.Difficulty.EASY).
                build();
    }

    public static SudokuPuzzle createTestSudokuPuzzleB() {
        return SudokuPuzzle.builder().
                initialBoardState("070004000000006900000329000007008010680932075090400600000295000002100000000800020").
                solution("973584261428716953516329847247658319681932475395471682764295138832167594159843726").
                difficulty(SudokuPuzzle.Difficulty.EXTREME).
                build();
    }

    public static SudokuPuzzle createTestSudokuPuzzleC() {
        return SudokuPuzzle.builder().
                initialBoardState("050000021630020800020937005000095000902704603000260000200853040005040086840000030").
                solution("759486321634521897128937465416395278982714653573268914267853149395142786841679532").
                difficulty(SudokuPuzzle.Difficulty.MEDIUM).
                build();
    }

    public static Lobby createTestLobbyA(User user) {
        HashSet<User> set = new HashSet<>();
        set.add(user);
        return Lobby.builder().
                lobbyName("Guru Lobby").
                isActive(true).
                isPublic(true).
                users(set).
                build();
    }

    public static Lobby createTestLobbyB(User userA, User userB) {
        HashSet<User> set = new HashSet<>();
        set.add(userA);
        set.add(userB);
        return Lobby.builder().
                lobbyName("SudokuSquad").
                isActive(true).
                isPublic(false).
                users(set).
                build();
    }

    public static Lobby createTestLobbyC(User userA, User userB, User userC) {
        HashSet<User> set = new HashSet<>();
        set.add(userA);
        set.add(userB);
        set.add(userC);
        return Lobby.builder().
                lobbyName("Active").
                isActive(false).
                isPublic(false).
                users(set).
                build();
    }

    public static LobbyState createTestLobbyStateA(final Lobby lobby, final User user, final SudokuPuzzle sudokuPuzzle) {
        return LobbyState.builder().
                lobby(lobby).
                user(user).
                puzzle(sudokuPuzzle).
                currentBoardState("092306001007008003043207080035680000080000020000035670070801950200500800500409130").
                score(0).
                build();
    }

    public static LobbyState createTestLobbyStateB(final Lobby lobby, final User user, final SudokuPuzzle sudokuPuzzle) {
        return LobbyState.builder().
                lobby(lobby).
                user(user).
                puzzle(sudokuPuzzle).
                currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020").
                score(10).
                build();
    }

    public static LobbyState createTestLobbyStateC(final Lobby lobby, final User user, final SudokuPuzzle sudokuPuzzle) {
        return LobbyState.builder().
                lobby(lobby).
                user(user).
                puzzle(sudokuPuzzle).
                currentBoardState("750000021630020800020937005000095000902704603000260000200853040005040086840000030").
                score(5).
                build();
    }

}
