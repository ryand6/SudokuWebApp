package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.GameEntity;
import com.github.ryand6.sudokuweb.domain.GameStateEntity;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.dto.GameDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.GameStateRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class GameEntityDtoMapper implements EntityDtoMapper<GameEntity, GameDto> {

    private final LobbyRepository lobbyRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final GameStateRepository gameStateRepository;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final SudokuPuzzleEntityDtoMapper sudokuPuzzleEntityDtoMapper;
    private final GameStateEntityDtoMapper gameStateEntityDtoMapper;

    public GameEntityDtoMapper(LobbyRepository lobbyRepository,
                               SudokuPuzzleRepository sudokuPuzzleRepository,
                               GameStateRepository gameStateRepository,
                               LobbyEntityDtoMapper lobbyEntityDtoMapper,
                               SudokuPuzzleEntityDtoMapper sudokuPuzzleEntityDtoMapper,
                               GameStateEntityDtoMapper gameStateEntityDtoMapper) {
        this.lobbyRepository = lobbyRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.gameStateRepository = gameStateRepository;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.sudokuPuzzleEntityDtoMapper = sudokuPuzzleEntityDtoMapper;
        this.gameStateEntityDtoMapper = gameStateEntityDtoMapper;
    }

    @Override
    public GameDto mapToDto(GameEntity gameEntity) {
        return GameDto.builder()
                .id(gameEntity.getId())
                .lobby(lobbyEntityDtoMapper.mapToDto(gameEntity.getLobbyEntity()))
                .sudokuPuzzle(sudokuPuzzleEntityDtoMapper.mapToDto(gameEntity.getSudokuPuzzleEntity()))
                .gameStates(
                        gameEntity.getGameStateEntities().stream()
                                .map(gameStateEntityDtoMapper::mapToDto)
                                .collect(Collectors.toSet())
                        )
                .build();
    }

//    public GameEntity mapFromDto(GameDto gameDto) {
//        LobbyEntity lobbyEntity = resolveDtoLobby(gameDto.getLobby().getId());
//        SudokuPuzzleEntity sudokuPuzzleEntity = resolveDtoPuzzle(gameDto.getSudokuPuzzle().getId());
//
//        GameEntity.GameEntityBuilder gameEntityBuilder = GameEntity.builder()
//                .lobbyEntity(lobbyEntity)
//                .sudokuPuzzleEntity(sudokuPuzzleEntity)
//                .gameStateEntities(
//                        gameDto.getGameStates().stream()
//                                .map(gameStateDto -> resolveDtoGameState(gameStateDto.getId()))
//                                .collect(Collectors.toSet())
//                );
//
//        // Don't assign id field if non-existent, DB will create
//        if (gameDto.getId() != null) {
//            gameEntityBuilder.id(gameDto.getId());
//        }
//
//        return gameEntityBuilder.build();
//
//    }
//
//    // Get LobbyEntity through DTO LobbyId
//    private LobbyEntity resolveDtoLobby(Long lobbyId) {
//        return lobbyRepository.findById(lobbyId)
//                .orElseThrow(() -> new EntityNotFoundException("Lobby not found with id " + lobbyId));
//    }
//
//    // Get SudokuPuzzleEntity through DTO PuzzleId
//    private SudokuPuzzleEntity resolveDtoPuzzle(Long puzzleId) {
//        return sudokuPuzzleRepository.findById(puzzleId)
//                .orElseThrow(() -> new EntityNotFoundException("Puzzle not found with id " + puzzleId));
//    }
//
//    private GameStateEntity resolveDtoGameState(Long gameStateId) {
//        return gameStateRepository.findById(gameStateId)
//                .orElseThrow(() -> new EntityNotFoundException("Game state not found with id " + gameStateId));
//    }

}
