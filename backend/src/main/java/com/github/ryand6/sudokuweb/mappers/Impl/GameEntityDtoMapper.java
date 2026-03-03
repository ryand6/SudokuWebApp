package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateRepository;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class GameEntityDtoMapper implements EntityDtoMapper<GameEntity, GameDto> {

    private final LobbyRepository lobbyRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final GamePlayerStateRepository gamePlayerStateRepository;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final SudokuPuzzleEntityDtoMapper sudokuPuzzleEntityDtoMapper;
    private final GameStateEntityDtoMapper gameStateEntityDtoMapper;

    public GameEntityDtoMapper(LobbyRepository lobbyRepository,
                               SudokuPuzzleRepository sudokuPuzzleRepository,
                               GamePlayerStateRepository gamePlayerStateRepository,
                               LobbyEntityDtoMapper lobbyEntityDtoMapper,
                               SudokuPuzzleEntityDtoMapper sudokuPuzzleEntityDtoMapper,
                               GameStateEntityDtoMapper gameStateEntityDtoMapper) {
        this.lobbyRepository = lobbyRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.gamePlayerStateRepository = gamePlayerStateRepository;
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
