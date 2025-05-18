package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LobbyEntityDtoMapper implements EntityDtoMapper<LobbyEntity, LobbyDto> {

    private final UserRepository userRepository;
    private final UserEntityDtoMapper userEntityDtoMapper;

    public LobbyEntityDtoMapper(UserRepository userRepository, UserEntityDtoMapper userEntityDtoMapper) {
        this.userRepository = userRepository;
        this.userEntityDtoMapper = userEntityDtoMapper;
    }

    @Override
    public LobbyDto mapToDto(LobbyEntity lobbyEntity) {
        return LobbyDto.builder()
                .id(lobbyEntity.getId())
                .lobbyName(lobbyEntity.getLobbyName())
                .isActive(lobbyEntity.getIsActive())
                .isPublic(lobbyEntity.getIsPublic())
                // Convert lobby entities to dtos and add to set
                .users(
                        lobbyEntity.getUserEntities().stream()
                                .map(userEntityDtoMapper::mapToDto)
                                .collect(Collectors.toSet())
                )
                .build();
    }

    @Override
    public LobbyEntity mapFromDto(LobbyDto lobbyDto) {
        // Get set of userEntities from dtos
        Set<UserEntity> userEntities = resolveDtoUsers(lobbyDto.getUsers());

        LobbyEntity.LobbyEntityBuilder lobbyEntityBuilder = LobbyEntity.builder()
                .lobbyName(lobbyDto.getLobbyName())
                .isActive(lobbyDto.getIsActive())
                .isPublic(lobbyDto.getIsPublic())
                .userEntities(userEntities);

        // Don't assign id field if non-existent, DB will create
        if (lobbyDto.getId() != null) {
            lobbyEntityBuilder.id(lobbyDto.getId());
        }

        return lobbyEntityBuilder.build();
    }

    // Get set of UserEntities associated with a set of UserDtos
    private Set<UserEntity> resolveDtoUsers(Set<UserDto> userDtos) {
        return userDtos.stream()
                .map(dto -> userRepository.findById(dto.getId())
                        .orElseThrow(() -> new EntityNotFoundException("User id not found: " + dto.getId())))
                .collect(Collectors.toSet());
    }
}
