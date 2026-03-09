package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateFactory;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;


public class GameFactory {

    public final LobbyRepository lobbyRepository;

    public GameFactory(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public static GameEntity createGame(LobbyEntity lobbyEntity, SudokuPuzzleEntity sudokuPuzzleEntity) {

        GameEntity newGame = new GameEntity();
        newGame.setLobbyEntity(lobbyEntity);
        newGame.setSudokuPuzzleEntity(sudokuPuzzleEntity);

        String initialBoardState = sudokuPuzzleEntity.getInitialBoardState();

        boolean isGameStateShared = newGame.isGameStateShared();

        if (isGameStateShared) {
            SharedGameStateEntity sharedGameState = SharedGameStateFactory.createSharedGameState(newGame, initialBoardState);
            newGame.setSharedGameStateEntity(sharedGameState);
        }

        return newGame;
    }

}
