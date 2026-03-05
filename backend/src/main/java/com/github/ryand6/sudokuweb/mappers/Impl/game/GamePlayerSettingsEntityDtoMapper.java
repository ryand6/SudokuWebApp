package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.player.settings.GamePlayerSettingsEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerSettingsDto;
import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerStateDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;

public class GamePlayerSettingsEntityDtoMapper implements EntityDtoMapper<GamePlayerSettingsEntity, GamePlayerSettingsDto> {

    @Override
    public GamePlayerSettingsDto mapToDto(GamePlayerSettingsEntity gamePlayerSettings) {
        return GamePlayerSettingsDto.builder()
                .showOtherPlayerHighlightedSquares(gamePlayerSettings.isShowOtherPlayerHighlightedSquares())
                .build();
    }

}
