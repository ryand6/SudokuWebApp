package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateFactory;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
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

        LobbySettingsEntity lobbySettings = lobbyEntity.getLobbySettingsEntity();
        GameSettingsEntity gameSettings = new GameSettingsEntity();
        gameSettings.setDifficulty(lobbySettings.getDifficulty());
        gameSettings.setTimeLimit(lobbySettings.getTimeLimit());
        gameSettings.setGameMode(lobbySettings.getGameMode());
        gameSettings.setGameType(lobbySettings.getGameType());
        newGame.setGameSettingsEntity(gameSettings);
        gameSettings.setGameEntity(newGame);

        String initialBoardState = sudokuPuzzleEntity.getInitialBoardState();

        boolean isGameStateShared = newGame.isBoardStateShared();

        if (isGameStateShared) {
            SharedGameStateEntity sharedGameState = SharedGameStateFactory.createSharedGameStateWithSharedBoardState(newGame, initialBoardState);
            newGame.setSharedGameStateEntity(sharedGameState);
        } else {
            SharedGameStateEntity sharedGameState = SharedGameStateFactory.createSharedGameStateWithoutSharedBoardState(newGame);
            newGame.setSharedGameStateEntity(sharedGameState);
        }

        return newGame;
    }

}
