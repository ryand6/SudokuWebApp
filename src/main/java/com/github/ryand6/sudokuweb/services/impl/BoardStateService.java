package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyStateEntity;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.GenerateBoardRequestDto;
import com.github.ryand6.sudokuweb.dto.GenerateBoardResponseDto;
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
    private final SudokuPuzzleRepository sudokuPuzzleRepository;

    public BoardStateService(PuzzleGenerator puzzleGenerator, LobbyRepository lobbyRepository, SudokuPuzzleRepository sudokuPuzzleRepository) {
        this.puzzleGenerator = puzzleGenerator;
        this.lobbyRepository = lobbyRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
    }

    /* Generate a new puzzle for the current lobby and creating lobbyState records for each
    active user in the lobby for the new puzzle - Transactional applied as multiple entities are
    saved to DB */
    @Transactional
    public GenerateBoardResponseDto generateSudokuBoard(GenerateBoardRequestDto request) {

        String difficulty = request.getDifficulty();
        Long lobbyId = request.getLobbyId();

        // Call static method to generate puzzle, retrieving both the puzzle and solution as a string
        // interpretation of a nested int array
        List<String> sudokuPuzzle = puzzleGenerator.generatePuzzle(difficulty);
        String puzzle = sudokuPuzzle.get(0);
        String solution = sudokuPuzzle.get(1);

        // Fetch associated lobby the puzzle is generated for
        LobbyEntity lobbyEntity = lobbyRepository.findById(lobbyId).orElseThrow(() -> new EntityNotFoundException("Lobby not found"));

        // Retrieve all active users in the lobby
        Set<UserEntity> activeUserEntities = lobbyEntity.getUserEntities();

        // Create SudokuPuzzle object
        SudokuPuzzleEntity newPuzzle = new SudokuPuzzleEntity();
        newPuzzle.setDifficulty(SudokuPuzzleEntity.Difficulty.valueOf(difficulty.toUpperCase()));
        newPuzzle.setInitialBoardState(puzzle);
        newPuzzle.setSolution(solution);

        // Create LobbyState objects for each active user in the lobby
        Set<LobbyStateEntity> lobbyStateEntities = new HashSet<>();
        for (UserEntity userEntity : activeUserEntities) {
            LobbyStateEntity state = new LobbyStateEntity();
            state.setUserEntity(userEntity);
            state.setLobbyEntity(lobbyEntity);
            state.setPuzzle(newPuzzle);
            // Board state starts with the initial puzzle
            state.setCurrentBoardState(puzzle);
            // Initial score for each user is 0
            state.setScore(0);
            lobbyStateEntities.add(state);
        }

        // Add lobby states to puzzle object
        newPuzzle.setLobbyStateEntities(lobbyStateEntities);

        // Save puzzle object to DB - will also save lobby states to DB due to cascade rules
        sudokuPuzzleRepository.save(newPuzzle);

        return new GenerateBoardResponseDto(puzzle, solution);
    }
}
