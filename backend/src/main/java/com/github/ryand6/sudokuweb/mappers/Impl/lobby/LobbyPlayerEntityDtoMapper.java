package com.github.ryand6.sudokuweb.mappers.Impl.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.user.UserEntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LobbyPlayerEntityDtoMapper implements EntityDtoMapper<LobbyPlayerEntity, LobbyPlayerDto> {

    private final UserEntityDtoMapper userEntityDtoMapper;

    public LobbyPlayerEntityDtoMapper(UserEntityDtoMapper userEntityDtoMapper) {
        this.userEntityDtoMapper = userEntityDtoMapper;
    }

    @Override
    public LobbyPlayerDto mapToDto(LobbyPlayerEntity lobbyPlayerEntity) {
        return LobbyPlayerDto.builder()
                .id(lobbyPlayerEntity.getId())
                .user(userEntityDtoMapper.mapToDto(lobbyPlayerEntity.getUser()))
                .joinedAt(lobbyPlayerEntity.getJoinedAt())
                .lobbyStatus(lobbyPlayerEntity.getLobbyStatus())
                .readyAt(lobbyPlayerEntity.getReadyAt())
                .lastLobbyMessageTimestamp(lobbyPlayerEntity.getLastLobbyMessageTimestamp())
                .build();
    }

}
