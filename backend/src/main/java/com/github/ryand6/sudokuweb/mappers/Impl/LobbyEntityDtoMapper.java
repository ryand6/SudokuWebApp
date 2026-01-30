package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LobbyEntityDtoMapper implements EntityDtoMapper<LobbyEntity, LobbyDto> {

    private final UserRepository userRepository;
    private final LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper;
    private final UserEntityDtoMapper userEntityDtoMapper;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public LobbyEntityDtoMapper(UserRepository userRepository,
                                LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper,
                                UserEntityDtoMapper userEntityDtoMapper,
                                LobbyPlayerRepository lobbyPlayerRepository) {
        this.userRepository = userRepository;
        this.lobbyPlayerEntityDtoMapper = lobbyPlayerEntityDtoMapper;
        this.userEntityDtoMapper = userEntityDtoMapper;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
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
                .difficulty(lobbyEntity.getDifficulty())
                .timeLimit(lobbyEntity.getTimeLimit())
                .isActive(lobbyEntity.getIsActive())
                .isPublic(lobbyEntity.getIsPublic())
                .inGame(lobbyEntity.getInGame())
                .currentGameId(lobbyEntity.getCurrentGameId())
                .countdownActive(lobbyEntity.getCountdownActive())
                .countdownEndsAt(lobbyEntity.getCountdownEndsAt())
                .countdownInitiatedBy(lobbyEntity.getCountdownInitiatedBy())
                .settingsLocked(lobbyEntity.getSettingsLocked())
                .lobbyPlayers(lobbyPlayerDtos)
                .host(userEntityDtoMapper.mapToDto(lobbyEntity.getHost()))
                .build();
    }

}
