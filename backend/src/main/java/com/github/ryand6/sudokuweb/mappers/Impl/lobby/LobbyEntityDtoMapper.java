package com.github.ryand6.sudokuweb.mappers.Impl.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.user.UserEntityDtoMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LobbyEntityDtoMapper implements EntityDtoMapper<LobbyEntity, LobbyDto> {

    private final LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper;
    private final UserEntityDtoMapper userEntityDtoMapper;
    private final LobbySettingsEntityDtoMapper lobbySettingsEntityDtoMapper;
    private final LobbyCountdownEntityDtoMapper lobbyCountdownEntityDtoMapper;

    public LobbyEntityDtoMapper(LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper,
                                UserEntityDtoMapper userEntityDtoMapper,
                                LobbySettingsEntityDtoMapper lobbySettingsEntityDtoMapper,
                                LobbyCountdownEntityDtoMapper lobbyCountdownEntityDtoMapper) {
        this.lobbyPlayerEntityDtoMapper = lobbyPlayerEntityDtoMapper;
        this.userEntityDtoMapper = userEntityDtoMapper;
        this.lobbySettingsEntityDtoMapper = lobbySettingsEntityDtoMapper;
        this.lobbyCountdownEntityDtoMapper = lobbyCountdownEntityDtoMapper;
    }

    @Override
    public LobbyDto mapToDto(LobbyEntity lobbyEntity) {
        // Convert lobby entities to dtos and add to set
        Set<LobbyPlayerDto> lobbyPlayerDtos = new HashSet<>();
        if (lobbyEntity.getLobbyPlayers() != null) {
            lobbyPlayerDtos = lobbyEntity.getLobbyPlayers().stream()
                    .map(lobbyPlayerEntityDtoMapper::mapToDto)
                    .collect(Collectors.toSet());
        }

        return LobbyDto.builder()
                .id(lobbyEntity.getId())
                .lobbyName(lobbyEntity.getLobbyName())
                .createdAt(lobbyEntity.getCreatedAt())
                .isActive(lobbyEntity.isActive())
                .inGame(lobbyEntity.isInGame())
                .currentGameId(lobbyEntity.getCurrentGameId())
                .lobbySettings(lobbySettingsEntityDtoMapper.mapToDto(lobbyEntity.getLobbySettingsEntity()))
                .lobbyCountdown(lobbyCountdownEntityDtoMapper.mapToDto(lobbyEntity.getLobbyCountdownEntity()))
                .lobbyPlayers(lobbyPlayerDtos)
                .host(userEntityDtoMapper.mapToDto(lobbyEntity.getHost()))
                .build();
    }

}
