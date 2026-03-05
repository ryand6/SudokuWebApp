package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.user.UserEntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class GamePlayerEntityDtoMapper implements EntityDtoMapper<GamePlayerEntity, GamePlayerDto> {

    private final UserEntityDtoMapper userEntityDtoMapper;
    private final GamePlayerStateEntityDtoMapper gamePlayerStateEntityDtoMapper;
    private final GamePlayerSettingsEntityDtoMapper gamePlayerSettingsEntityDtoMapper;

    public GamePlayerEntityDtoMapper(UserEntityDtoMapper userEntityDtoMapper, GamePlayerStateEntityDtoMapper gamePlayerStateEntityDtoMapper, GamePlayerSettingsEntityDtoMapper gamePlayerSettingsEntityDtoMapper) {
        this.userEntityDtoMapper = userEntityDtoMapper;
        this.gamePlayerStateEntityDtoMapper = gamePlayerStateEntityDtoMapper;
        this.gamePlayerSettingsEntityDtoMapper = gamePlayerSettingsEntityDtoMapper;
    }

    @Override
    public GamePlayerDto mapToDto(GamePlayerEntity gamePlayer) {
        return GamePlayerDto.builder()
                .user(userEntityDtoMapper.mapToDto(gamePlayer.getUserEntity()))
                .gamePlayerState(gamePlayerStateEntityDtoMapper.mapToDto(gamePlayer.getGamePlayerStateEntity()))
                .gamePlayerSettings(gamePlayerSettingsEntityDtoMapper.mapToDto(gamePlayer.getGamePlayerSettingsEntity()))
                .playerColour(gamePlayer.getPlayerColour())
                .score(gamePlayer.getScore())
                .gameLoaded(gamePlayer.isGameLoaded())
                .gameResult(gamePlayer.getGameResult())
                .build();
    }

}
