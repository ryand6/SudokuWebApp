package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.domain.Lobby;
import com.github.ryand6.sudokuweb.domain.LobbyState;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import com.github.ryand6.sudokuweb.domain.User;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.services.PuzzleGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
public class BoardStateController {

    private final PuzzleGenerator puzzleGenerator;
    private final LobbyRepository lobbyRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;

    public BoardStateController(PuzzleGenerator puzzleGenerator, LobbyRepository lobbyRepository, SudokuPuzzleRepository sudokuPuzzleRepository) {
        this.puzzleGenerator = puzzleGenerator;
        this.lobbyRepository = lobbyRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
    }

    /* Generate a new puzzle for the current lobby and creating lobbyState records for each
    active user in the lobby for the new puzzle */
    @GetMapping("/generate-board")
    public List<String> generateSudokuBoard(@RequestParam String difficulty, @RequestParam Long lobbyId) {

        // Call static method to generate puzzle, retrieving both the puzzle and solution as a string
        // interpretation of a nested int array
        List<String> sudokuPuzzle = puzzleGenerator.generatePuzzle(difficulty);
        String puzzle = sudokuPuzzle.get(0);
        String solution = sudokuPuzzle.get(1);

        // Fetch associated lobby the puzzle is generated for
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(() -> new EntityNotFoundException("Lobby not found"));

        // Retrieve all active users in the lobby
        Set<User> activeUsers = lobby.getUsers();

        // Create SudokuPuzzle object
        SudokuPuzzle newPuzzle = new SudokuPuzzle();
        newPuzzle.setDifficulty(SudokuPuzzle.Difficulty.valueOf(difficulty.toUpperCase()));
        newPuzzle.setInitialBoardState(puzzle);
        newPuzzle.setSolution(solution);

        // Create LobbyState objects for each active user in the lobby
        Set<LobbyState> lobbyStates = new HashSet<>();
        for (User user: activeUsers) {
            LobbyState state = new LobbyState();
            state.setUser(user);
            state.setLobby(lobby);
            state.setPuzzle(newPuzzle);
            // Board state starts with the initial puzzle
            state.setCurrentBoardState(puzzle);
            // Initial score for each user is 0
            state.setScore(0);
            lobbyStates.add(state);
        }

        // Add lobby states to puzzle object
        newPuzzle.setLobbyStates(lobbyStates);

        // Save puzzle object to DB - will also save lobby states to DB due to cascde rules
        sudokuPuzzleRepository.save(newPuzzle);

        return sudokuPuzzle;
    }

}
