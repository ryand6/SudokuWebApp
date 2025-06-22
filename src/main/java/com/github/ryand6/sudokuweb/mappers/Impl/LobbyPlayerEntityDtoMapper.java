package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.LobbyPlayerDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class LobbyPlayerEntityDtoMapper implements EntityDtoMapper<LobbyPlayerEntity, LobbyPlayerDto> {

    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;

    public LobbyPlayerEntityDtoMapper(UserRepository userRepository,
                                      LobbyRepository lobbyRepository) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
    }

    @Override
    public LobbyPlayerDto mapToDto(LobbyPlayerEntity lobbyPlayerEntity) {
        return LobbyPlayerDto.builder()
                .id(lobbyPlayerEntity.getId())
                .joinedAt(lobbyPlayerEntity.getJoinedAt())
                .build();
    }

    @Override
    public LobbyPlayerEntity mapFromDto(LobbyPlayerDto lobbyPlayerDto) {
        UserEntity userEntity = resolveDtoUser(lobbyPlayerDto.getId().getUserId());
        LobbyEntity lobbyEntity = resolveDtoLobby(lobbyPlayerDto.getId().getLobbyId());

        return LobbyPlayerEntity.builder()
                .id(lobbyPlayerDto.getId())
                .lobby(lobbyEntity)
                .user(userEntity)
                .joinedAt(lobbyPlayerDto.getJoinedAt())
                .build();
    }

    // Get UserEntity from DTO composite key ID
    private UserEntity resolveDtoUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User id not found: " + userId));
    }

    // Get LobbyEntity from DTO composite key ID
    private LobbyEntity resolveDtoLobby(Long lobbyId) {
        return lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new EntityNotFoundException("Lobby id not found: " + lobbyId));
    }

}
