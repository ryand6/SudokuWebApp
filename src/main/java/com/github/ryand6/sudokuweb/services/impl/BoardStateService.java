package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.domain.factory.GameFactory;
import com.github.ryand6.sudokuweb.domain.factory.SudokuPuzzleFactory;
import com.github.ryand6.sudokuweb.dto.GameDto;
import com.github.ryand6.sudokuweb.dto.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.exceptions.InvalidDifficultyException;
import com.github.ryand6.sudokuweb.exceptions.TooManyActivePlayersException;
import com.github.ryand6.sudokuweb.mappers.Impl.GameEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.GameRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.services.PuzzleGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class BoardStateService {

    private static final Logger log = LoggerFactory.getLogger(BoardStateService.class);

    private final PuzzleGenerator puzzleGenerator;
    private final LobbyRepository lobbyRepository;
    private final GameRepository gameRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final GameEntityDtoMapper gameEntityDtoMapper;

    public BoardStateService(PuzzleGenerator puzzleGenerator,
                             LobbyRepository lobbyRepository,
                             GameRepository gameRepository,
                             SudokuPuzzleRepository sudokuPuzzleRepository,
                             GameEntityDtoMapper gameEntityDtoMapper) {
        this.puzzleGenerator = puzzleGenerator;
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.gameEntityDtoMapper = gameEntityDtoMapper;
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity - Transactional applied as multiple entities are
    saved to DB */
    @Transactional
    public GameDto createGame(GenerateBoardRequestDto request) {

        String difficulty = request.getDifficulty();
        Long lobbyId = request.getLobbyId();

        // Fetch the lobby that is creating the game
        LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyId).orElseThrow(() -> new EntityNotFoundException("Lobby with ID " + lobbyId + " not found when creating game."));

        // Retrieve all active lobbyPlayers in the game
        Set<LobbyPlayerEntity> activeLobbyPlayers = lobbyEntity.getLobbyPlayers();

        if (activeLobbyPlayers.size() > 4) {
            throw new TooManyActivePlayersException("Cannot create game: Lobby with id " + lobbyId + " has more than 4 active players.");
        }

        Difficulty diffEnum;
        try {
            diffEnum = Difficulty.valueOf(difficulty.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidDifficultyException("Invalid difficulty value: " + difficulty);
        }

        log.info("Created new game for lobby ID {} with player count {} and difficulty {}", lobbyId, activeLobbyPlayers.size(), difficulty);

        // Call static method to generate sudokuPuzzleEntity, retrieving both the sudokuPuzzleEntity and solution as a string
        // interpretation of a nested int array
        List<String> sudokuPuzzle = puzzleGenerator.generatePuzzle(difficulty);
        String puzzle = new String(sudokuPuzzle.get(0));
        String solution = new String(sudokuPuzzle.get(1));

        // Create SudokuPuzzle object
        SudokuPuzzleEntity newPuzzle = SudokuPuzzleFactory.createSudokuPuzzle(puzzle, solution, diffEnum);
        sudokuPuzzleRepository.save(newPuzzle);

        GameEntity newGame = GameFactory.createGame(lobbyEntity, newPuzzle);
        gameRepository.save(newGame);

        return gameEntityDtoMapper.mapToDto(newGame);
    }
}
