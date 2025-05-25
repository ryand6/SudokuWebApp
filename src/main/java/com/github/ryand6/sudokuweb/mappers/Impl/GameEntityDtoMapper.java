package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.dto.GameDto;
import com.github.ryand6.sudokuweb.dto.GameStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class GameEntityDtoMapper implements EntityDtoMapper<GameEntity, GameDto> {

    private final LobbyRepository lobbyRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;

    public GameEntityDtoMapper(LobbyRepository lobbyRepository, UserRepository userRepository, SudokuPuzzleRepository sudokuPuzzleRepository) {
        this.lobbyRepository = lobbyRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
    }


    // Get LobbyEntity through DTO LobbyId
    private LobbyEntity resolveDtoLobby(Long lobbyId) {
        return lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new EntityNotFoundException("Lobby not found with id " + lobbyId));
    }

    // Get SudokuPuzzleEntity through DTO PuzzleId
    private SudokuPuzzleEntity resolveDtoPuzzle(Long puzzleId) {
        return sudokuPuzzleRepository.findById(puzzleId)
                .orElseThrow(() -> new EntityNotFoundException("Puzzle not found with id " + puzzleId));
    }

}
