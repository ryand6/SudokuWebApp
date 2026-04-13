package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerDto;
import com.github.ryand6.sudokuweb.enums.CellStatus;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.user.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.util.GameUtils;
import org.springframework.stereotype.Component;

@Component
public class GamePlayerEntityDtoMapper implements EntityDtoMapper<GamePlayerEntity, GamePlayerDto> {

    private final UserEntityDtoMapper userEntityDtoMapper;

    public GamePlayerEntityDtoMapper(UserEntityDtoMapper userEntityDtoMapper) {
        this.userEntityDtoMapper = userEntityDtoMapper;
    }

    @Override
    public GamePlayerDto mapToDto(GamePlayerEntity gamePlayer) {
        String currentBoardState = gamePlayer.getGamePlayerStateEntity().getCurrentBoardState();
        CellStatus[] boardProgress = currentBoardState != null ? GameUtils.convertBoardStateIntoProgressState(gamePlayer.getGamePlayerStateEntity().getCurrentBoardState(), gamePlayer.getGameEntity().getSudokuPuzzleEntity().getInitialBoardState()) : null;
        return GamePlayerDto.builder()
                .user(userEntityDtoMapper.mapToDto(gamePlayer.getUserEntity()))
                .playerColour(gamePlayer.getPlayerColour())
                .boardProgress(boardProgress)
                .score(gamePlayer.getScore())
                .firsts(gamePlayer.getFirsts())
                .mistakes(gamePlayer.getMistakes())
                .maxStreak(gamePlayer.getMaxStreak())
                .gameLoaded(gamePlayer.isGameLoaded())
                .gameResult(gamePlayer.getGameResult())
                .build();
    }

}
