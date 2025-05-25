package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.dto.GameDto;
import com.github.ryand6.sudokuweb.dto.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.dto.GenerateBoardResponseDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import com.github.ryand6.sudokuweb.exceptions.TooManyActivePlayersException;
import com.github.ryand6.sudokuweb.repositories.GameRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.services.PuzzleGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BoardStateService {

    private final PuzzleGenerator puzzleGenerator;
    private final LobbyRepository lobbyRepository;
    private final GameRepository gameRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;

    public BoardStateService(PuzzleGenerator puzzleGenerator,
                             LobbyRepository lobbyRepository,
                             GameRepository gameRepository,
                             SudokuPuzzleRepository sudokuPuzzleRepository) {
        this.puzzleGenerator = puzzleGenerator;
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity - Transactional applied as multiple entities are
    saved to DB */
    @Transactional
    public GameDto createGame(GenerateBoardRequestDto request) {

        String difficulty = request.getDifficulty();
        Long lobbyId = request.getLobbyId();

        // Call static method to generate sudokuPuzzleEntity, retrieving both the sudokuPuzzleEntity and solution as a string
        // interpretation of a nested int array
        List<String> sudokuPuzzle = puzzleGenerator.generatePuzzle(difficulty);
        String puzzle = sudokuPuzzle.get(0);
        String solution = sudokuPuzzle.get(1);

        // Fetch associated game the sudokuPuzzleEntity is generated for
        LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyId).orElseThrow(() -> new EntityNotFoundException("Game not found"));

        // Retrieve all active users in the game
        Set<UserEntity> activeUserEntities = lobbyEntity.getUserEntities();

        if (activeUserEntities.size() > 4) {
            throw new TooManyActivePlayersException("Cannot create game: Lobby with id " + lobbyId + " has more than 4 active players.")
        }

        // Create SudokuPuzzle object
        SudokuPuzzleEntity newPuzzle = new SudokuPuzzleEntity();
        newPuzzle.setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));
        newPuzzle.setInitialBoardState(puzzle);
        newPuzzle.setSolution(solution);
        sudokuPuzzleRepository.save(newPuzzle);

        GameEntity newGame = new GameEntity();
        newGame.setLobbyEntity(lobbyEntity);
        newGame.setSudokuPuzzleEntity(newPuzzle);

        // Get list of player colour enums
        PlayerColour[] playerColours = PlayerColour.values();
        int i = 0;

        // Create GameState objects for each active user in the game
        Set<GameStateEntity> gameStateEntities = new HashSet<>();
        for (UserEntity userEntity : activeUserEntities) {
            GameStateEntity state = new GameStateEntity();
            state.setUserEntity(userEntity);
            state.setGameEntity(newGame);
            // Board state starts with the initial sudokuPuzzleEntity
            state.setCurrentBoardState(puzzle);
            // Initial score for each user is 0
            state.setScore(0);
            // Set the player colour and increment the counter so the next player colour is unique
            state.setPlayerColour(playerColours[i]);
            i++;
            gameStateEntities.add(state);
        }

        // Will save gameState entities to DB also due to cascade rules
        newGame.setGameStateEntities(gameStateEntities);
        gameRepository.save(newGame);

        return new GenerateBoardResponseDto(puzzle, solution);
    }
}
