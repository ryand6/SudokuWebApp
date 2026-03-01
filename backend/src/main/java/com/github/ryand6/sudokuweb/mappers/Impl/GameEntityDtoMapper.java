package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.GameStateRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
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

}
