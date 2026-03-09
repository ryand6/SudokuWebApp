package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.game.SharedGameStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class GameEntityDtoMapper implements EntityDtoMapper<GameEntity, GameDto> {

    private final GamePlayerEntityDtoMapper gamePlayerEntityDtoMapper;
    private final SharedGameStateEntityDtoMapper sharedGameStateEntityDtoMapper;

    public GameEntityDtoMapper(GamePlayerEntityDtoMapper gamePlayerEntityDtoMapper, SharedGameStateEntityDtoMapper sharedGameStateEntityDtoMapper) {

        this.gamePlayerEntityDtoMapper = gamePlayerEntityDtoMapper;
        this.sharedGameStateEntityDtoMapper = sharedGameStateEntityDtoMapper;
    }

    @Override
    public GameDto mapToDto(GameEntity game) {
        LobbySettingsEntity lobbySettings = game.getLobbyEntity().getLobbySettingsEntity();

        SharedGameStateDto sharedGameStateDto = null;
        SharedGameStateEntity sharedGameStateEntity = game.getSharedGameStateEntity();

        if (game.getSharedGameStateEntity() != null) {
            sharedGameStateDto = sharedGameStateEntityDtoMapper.mapToDto(sharedGameStateEntity);
        }

        return GameDto.builder()
                .gameId(game.getId())
                .lobbyId(game.getLobbyEntity().getId())
                .gamePlayers(
                        game.getGamePlayerEntities().stream()
                                .map(gamePlayerEntityDtoMapper::mapToDto)
                                .collect(Collectors.toSet())
                )
                .sharedGameState(sharedGameStateDto)
                .initialBoardState(game.getSudokuPuzzleEntity().getInitialBoardState())
                .gameMode(lobbySettings.getGameMode())
                .difficulty(lobbySettings.getDifficulty())
                .timeLimit(lobbySettings.getTimeLimit())
                .gameStatus(game.getGameStatus())
                .gameStartsAt(game.getGameStartsAt())
                .gameEndsAt(game.getGameEndsAt())
                .build();
    }

}
