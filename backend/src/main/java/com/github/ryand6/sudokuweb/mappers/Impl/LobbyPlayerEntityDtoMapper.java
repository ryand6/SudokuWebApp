package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class LobbyPlayerEntityDtoMapper implements EntityDtoMapper<LobbyPlayerEntity, LobbyPlayerDto> {

    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final UserEntityDtoMapper userEntityDtoMapper;

    public LobbyPlayerEntityDtoMapper(UserRepository userRepository,
                                      LobbyRepository lobbyRepository, UserEntityDtoMapper userEntityDtoMapper) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
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
                .lobbyMessageTimestamp(lobbyPlayerEntity.getLobbyMessageTimestamp())
                .build();
    }

}
