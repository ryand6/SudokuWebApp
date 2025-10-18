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
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public LobbyEntityDtoMapper(UserRepository userRepository,
                                LobbyPlayerEntityDtoMapper lobbyPlayerEntityDtoMapper,
                                LobbyPlayerRepository lobbyPlayerRepository) {
        this.userRepository = userRepository;
        this.lobbyPlayerEntityDtoMapper = lobbyPlayerEntityDtoMapper;
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
                .countdownActive(lobbyEntity.getCountdownActive())
                .countdownEndsAt(lobbyEntity.getCountdownEndsAt())
                .countdownInitiatedBy(lobbyEntity.getCountdownInitiatedBy())
                .settingsLocked(lobbyEntity.getSettingsLocked())
                .lobbyPlayers(lobbyPlayerDtos)
                .hostId(lobbyEntity.getHost().getId())
                .build();
    }

//    public LobbyEntity mapFromDto(LobbyDto lobbyDto) {
//        // Get set of lobbyPlayerEntities from dtos
//        Set<LobbyPlayerEntity> lobbyPlayerEntities = resolveDtoLobbyPlayers(lobbyDto.getLobbyPlayers());
//        UserEntity hostEntity = resolveHostById(lobbyDto.getHostId());
//
//        LobbyEntity.LobbyEntityBuilder lobbyEntityBuilder = LobbyEntity.builder()
//                .lobbyName(lobbyDto.getLobbyName())
//                .difficulty(lobbyDto.getDifficulty())
//                .timeLimit(lobbyDto.getTimeLimit())
//                .isActive(lobbyDto.getIsActive())
//                .isPublic(lobbyDto.getIsPublic())
//                .countdownActive(lobbyDto.getCountdownActive())
//                .countdownEndsAt(lobbyDto.getCountdownEndsAt())
//                .countdownInitiatedBy(lobbyDto.getCountdownInitiatedBy())
//                .settingsLocked(lobbyDto.getSettingsLocked())
//                .lobbyPlayers(lobbyPlayerEntities)
//                .host(hostEntity);
//
//        // Don't assign id field if non-existent, DB will create
//        if (lobbyDto.getId() != null) {
//            lobbyEntityBuilder.id(lobbyDto.getId());
//        }
//
//        return lobbyEntityBuilder.build();
//    }
//
//    // Updates the LobbyEntity using the info provided by the DTO
//    public LobbyEntity mapFromDto(LobbyDto lobbyDto, LobbyEntity existingLobby) {
//        // Get set of lobbyPlayerEntities from dtos
//        Set<LobbyPlayerEntity> lobbyPlayerEntities = resolveDtoLobbyPlayers(lobbyDto.getLobbyPlayers());
//        UserEntity hostEntity = resolveHostById(lobbyDto.getHostId());
//
//        // Update mutable fields
//        existingLobby.setLobbyName(lobbyDto.getLobbyName());
//        existingLobby.setDifficulty(lobbyDto.getDifficulty());
//        existingLobby.setTimeLimit(lobbyDto.getTimeLimit());
//        existingLobby.setIsActive(lobbyDto.getIsActive());
//        existingLobby.setIsPublic(lobbyDto.getIsPublic());
//        existingLobby.setCountdownActive(lobbyDto.getCountdownActive());
//        existingLobby.setCountdownEndsAt(lobbyDto.getCountdownEndsAt());
//        existingLobby.setCountdownInitiatedBy(lobbyDto.getCountdownInitiatedBy());
//        existingLobby.setSettingsLocked(lobbyDto.getSettingsLocked());
//        existingLobby.setLobbyPlayers(lobbyPlayerEntities);
//        existingLobby.setHost(hostEntity);
//
//        // DON'T touch gameEntities, since they already exist and are managed elsewhere
//
//        return existingLobby;
//    }
//
//    // Get set of LobbyPlayerEntities associated with a set of LobbyPlayerDtos
//    private Set<LobbyPlayerEntity> resolveDtoLobbyPlayers(Set<LobbyPlayerDto> lobbyPlayerDtos) {
//        return lobbyPlayerDtos.stream()
//                .map(dto -> lobbyPlayerRepository.findById(dto.getId())
//                        .orElseThrow(() -> new EntityNotFoundException("Lobby Player id not found: " + dto.getId().toString())))
//                .collect(Collectors.toSet());
//    }
//
//    // Get UserEntity associated with the host's id
//    private UserEntity resolveHostById(Long hostId) {
//        return userRepository.findById(hostId)
//                .orElseThrow(() -> new EntityNotFoundException("User with host id not found: " + hostId));
//    }

}
