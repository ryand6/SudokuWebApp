package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class GamePlayerStateEntityDtoMapper implements EntityDtoMapper<GamePlayerStateEntity, GamePlayerStateDto> {

    @Override
    public GamePlayerStateDto mapToDto(GamePlayerStateEntity gamePlayerState) {
        return GamePlayerStateDto.builder()
                .currentBoardState(gamePlayerState.getCurrentBoardState())
                .notes(gamePlayerState.getNotes())
                .currentStreak(gamePlayerState.getCurrentStreak())
                .activeMultiplier(gamePlayerState.getActiveMultiplier())
                .multiplierEndsAt(gamePlayerState.getMultiplierEndsAt())
                .build();
    }

}
