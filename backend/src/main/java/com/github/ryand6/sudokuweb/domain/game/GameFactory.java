package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.Set;

public class GameFactory {

    public final LobbyRepository lobbyRepository;

    public GameFactory(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    @Transactional
    public static GameEntity createGame(LobbyEntity lobbyEntity, SudokuPuzzleEntity sudokuPuzzleEntity) {

        GameEntity newGame = new GameEntity();
        newGame.setLobbyEntity(lobbyEntity);
        newGame.setSudokuPuzzleEntity(sudokuPuzzleEntity);

        // Get list of player colour enums
        PlayerColour[] playerColours = PlayerColour.values();
        int i = 0;

        Set<LobbyPlayerEntity> activeLobbyPlayers = lobbyEntity.getLobbyPlayers();

        // Create GameState objects for each active user in the game
        Set<GameStateEntity> gameStateEntities = new HashSet<>();
        for (LobbyPlayerEntity lobbyPlayerEntity : activeLobbyPlayers) {
            GameStateEntity state = new GameStateEntity();
            state.setUserEntity(lobbyPlayerEntity.getUser());
            state.setGameEntity(newGame);
            // Board state starts with the initial sudokuPuzzleEntity
            state.setCurrentBoardState(sudokuPuzzleEntity.getInitialBoardState());
            // Initial score for each user is 0
            state.setScore(0);
            // Set the player colour and increment the counter so the next player colour is unique
            state.setPlayerColour(playerColours[i]);
            i++;
            gameStateEntities.add(state);
        }

        // Will save gameState entities to DB also due to cascade rules
        newGame.setGameStateEntities(gameStateEntities);

        return newGame;

    }

}
