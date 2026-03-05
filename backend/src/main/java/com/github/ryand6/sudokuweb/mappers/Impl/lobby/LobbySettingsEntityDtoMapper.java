package com.github.ryand6.sudokuweb.mappers.Impl.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbySettingsDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;

public class LobbySettingsEntityDtoMapper implements EntityDtoMapper<LobbySettingsEntity, LobbySettingsDto> {

    @Override
    public LobbySettingsDto mapToDto(LobbySettingsEntity lobbySettings) {
        return LobbySettingsDto.builder()
                .isPublic(lobbySettings.isPublic())
                .difficulty(lobbySettings.getDifficulty())
                .timeLimit(lobbySettings.getTimeLimit())
                .gameMode(lobbySettings.getGameMode())
                .build();
    }

}
