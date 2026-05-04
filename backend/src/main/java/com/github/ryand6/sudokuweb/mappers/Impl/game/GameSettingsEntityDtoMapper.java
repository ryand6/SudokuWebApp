
package com.github.ryand6.sudokuweb.mappers.Impl.game;

import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsEntity;
import com.github.ryand6.sudokuweb.dto.entity.game.GameSettingsDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class GameSettingsEntityDtoMapper implements EntityDtoMapper<GameSettingsEntity, GameSettingsDto> {

    @Override
    public GameSettingsDto mapToDto(GameSettingsEntity gameSettings) {
        return GameSettingsDto.builder()
                .difficulty(gameSettings.getDifficulty())
                .timeLimit(gameSettings.getTimeLimit())
                .gameMode(gameSettings.getGameMode())
                .gameType(gameSettings.getGameType())
                .build();
    }

}
