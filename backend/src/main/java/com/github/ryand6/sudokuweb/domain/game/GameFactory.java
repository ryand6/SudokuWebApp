package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerFactory;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateFactory;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;

import java.util.*;

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

        // Get list of player colour enums
        PlayerColour[] playerColours = PlayerColour.values();
        List<PlayerColour> colourList = Arrays.asList(playerColours);
        // Randomise order
        Collections.shuffle(colourList);
        playerColours = colourList.toArray(new PlayerColour[LobbyEntity.LOBBY_SIZE]);

        int i = 0;

        Set<LobbyPlayerEntity> lobbyPlayers = lobbyEntity.getLobbyPlayers();

        // Create GameState objects for each active user in the game
        Set<GamePlayerEntity> gamePlayers = new HashSet<>();

        for (LobbyPlayerEntity lobbyPlayerEntity : lobbyPlayers) {
            PlayerColour playerColour = playerColours[i];
            GamePlayerEntity gamePlayer = GamePlayerFactory.createGamePlayer(newGame, lobbyPlayerEntity.getUser(), playerColour, isGameStateShared, initialBoardState);
            gamePlayers.add(gamePlayer);
            i++;
        }

        // Will save GamePlayer entities to DB also due to cascade rules
        newGame.setGamePlayerEntities(gamePlayers);

        return newGame;
    }

}
