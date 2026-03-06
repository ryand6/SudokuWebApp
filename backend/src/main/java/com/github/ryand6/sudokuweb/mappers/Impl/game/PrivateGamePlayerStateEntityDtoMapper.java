package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.PrivateGamePlayerStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class PrivateGamePlayerStateEntityDtoMapper implements EntityDtoMapper<GamePlayerStateEntity, PrivateGamePlayerStateDto> {

    private final GamePlayerSettingsEntityDtoMapper gamePlayerSettingsEntityDtoMapper;

    public PrivateGamePlayerStateEntityDtoMapper(GamePlayerSettingsEntityDtoMapper gamePlayerSettingsEntityDtoMapper) {
        this.gamePlayerSettingsEntityDtoMapper = gamePlayerSettingsEntityDtoMapper;
    }

    @Override
    public PrivateGamePlayerStateDto mapToDto(GamePlayerStateEntity gamePlayerState) {
        return PrivateGamePlayerStateDto.builder()
                .currentBoardState(gamePlayerState.getCurrentBoardState())
                .notes(gamePlayerState.getNotes())
                .currentStreak(gamePlayerState.getCurrentStreak())
                .activeMultiplier(gamePlayerState.getActiveMultiplier())
                .multiplierEndsAt(gamePlayerState.getMultiplierEndsAt())
                .gamePlayerSettings(gamePlayerSettingsEntityDtoMapper.mapToDto(gamePlayerState.getGamePlayerEntity().getGamePlayerSettingsEntity()))
                .build();
    }

}
