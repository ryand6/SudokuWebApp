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
                .difficulty(lobbyEntity.getDifficulty())
                .isActive(lobbyEntity.getIsActive())
                .isPublic(lobbyEntity.getIsPublic())
                // Convert lobby entities to dtos and add to set
                .users(
                        lobbyEntity.getUserEntities().stream()
                                .map(userEntityDtoMapper::mapToDto)
                                .collect(Collectors.toSet())
                )
                .hostId(lobbyEntity.getHost().getId())
                .build();
    }

    @Override
    public LobbyEntity mapFromDto(LobbyDto lobbyDto) {
        // Get set of userEntities from dtos
        Set<UserEntity> userEntities = resolveDtoUsers(lobbyDto.getUsers());
        UserEntity hostEntity = resolveHostById(lobbyDto.getHostId());

        LobbyEntity.LobbyEntityBuilder lobbyEntityBuilder = LobbyEntity.builder()
                .lobbyName(lobbyDto.getLobbyName())
                .difficulty(lobbyDto.getDifficulty())
                .isActive(lobbyDto.getIsActive())
                .isPublic(lobbyDto.getIsPublic())
                .userEntities(userEntities)
                .host(hostEntity);

        // Don't assign id field if non-existent, DB will create
        if (lobbyDto.getId() != null) {
            lobbyEntityBuilder.id(lobbyDto.getId());
        }

        return lobbyEntityBuilder.build();
    }

    public LobbyEntity mapFromDto(LobbyDto lobbyDto, String joinCode) {
        // Get set of userEntities from dtos
        Set<UserEntity> userEntities = resolveDtoUsers(lobbyDto.getUsers());
        UserEntity hostEntity = resolveHostById(lobbyDto.getHostId());

        LobbyEntity.LobbyEntityBuilder lobbyEntityBuilder = LobbyEntity.builder()
                .lobbyName(lobbyDto.getLobbyName())
                .difficulty(lobbyDto.getDifficulty())
                .isActive(lobbyDto.getIsActive())
                .isPublic(lobbyDto.getIsPublic())
                .joinCode(joinCode)
                .userEntities(userEntities)
                .host(hostEntity);

        // Don't assign id field if non-existent, DB will create
        if (lobbyDto.getId() != null) {
            lobbyEntityBuilder.id(lobbyDto.getId());
        }

        return lobbyEntityBuilder.build();
    }

    // Updates the LobbyEntity using the info provided by the DTO
    public LobbyEntity mapFromDto(LobbyDto lobbyDto, LobbyEntity existingLobby) {
        Set<UserEntity> userEntities = resolveDtoUsers(lobbyDto.getUsers());
        UserEntity hostEntity = resolveHostById(lobbyDto.getHostId());

        // Update mutable fields
        existingLobby.setLobbyName(lobbyDto.getLobbyName());
        existingLobby.setDifficulty(lobbyDto.getDifficulty());
        existingLobby.setIsActive(lobbyDto.getIsActive());
        existingLobby.setIsPublic(lobbyDto.getIsPublic());
        existingLobby.setUserEntities(userEntities);
        existingLobby.setHost(hostEntity);

        // DON'T touch gameEntities, since they already exist and are managed elsewhere

        return existingLobby;
    }

    // Get set of UserEntities associated with a set of UserDtos
    private Set<UserEntity> resolveDtoUsers(Set<UserDto> userDtos) {
        return userDtos.stream()
                .map(dto -> userRepository.findById(dto.getId())
                        .orElseThrow(() -> new EntityNotFoundException("User id not found: " + dto.getId())))
                .collect(Collectors.toSet());
    }

    // Get UserEntity associated with the host's id
    private UserEntity resolveHostById(Long hostId) {
        return userRepository.findById(hostId)
                .orElseThrow(() -> new EntityNotFoundException("User with host id not found: " + hostId));
    }

}
