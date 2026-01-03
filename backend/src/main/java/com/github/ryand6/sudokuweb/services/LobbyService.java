package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.domain.factory.LobbyPlayerFactory;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import com.github.ryand6.sudokuweb.exceptions.*;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserService userService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final LobbyPlayerRepository lobbyPlayerRepository;
    private final PrivateLobbyTokenService privateLobbyTokenService;

    public LobbyService(LobbyRepository lobbyRepository,
                        UserService userService,
                        LobbyEntityDtoMapper lobbyEntityDtoMapper,
                        LobbyPlayerRepository lobbyPlayerRepository,
                        PrivateLobbyTokenService privateLobbyTokenService) {
        this.lobbyRepository = lobbyRepository;
        this.userService = userService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
        this.privateLobbyTokenService = privateLobbyTokenService;
    }

    @Transactional
    public LobbyDto createNewLobby(String lobbyName, Boolean isPublic, Long requesterId) {
        LobbyEntity newLobby = new LobbyEntity();
        // One of these must be true or there is an error with the parameters
        if (Boolean.TRUE.equals(isPublic)) {
            newLobby.setIsPublic(true);
        } else if (Boolean.FALSE.equals(isPublic)) {
            newLobby.setIsPublic(false);
        } else {
            throw new InvalidLobbyPublicStatusParameterException("Either Public or Private lobby must be checked");
        }
        UserEntity requester = userService.findUserById(requesterId);
        newLobby.setLobbyName(lobbyName);
        // requester of lobby creation becomes the host
        newLobby.setHost(requester);
        newLobby.setIsActive(true);
        // Save the lobby first so that it can then be referenced by the LobbyPlayerEntity to be attached to the new lobby
        lobbyRepository.saveAndFlush(newLobby);
        addPlayerToLobby(newLobby, requester);
        return lobbyEntityDtoMapper.mapToDto(newLobby);
    }

    // Retrieves a page of lobbies based on the specified page number and size. Page results are ordered by createdAt (newest first)
    public List<LobbyDto> getPublicLobbies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return lobbyRepository.findByIsPublicTrueAndIsActiveTrue(pageable)
                .stream()
                .map(lobbyEntityDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // Overloaded method, used to join public lobby when lobby ID is provided
    @Transactional
    public LobbyDto joinLobby(Long userId, Long publicLobbyId) {
        // Validate input parameters
        if (publicLobbyId == null || userId == null) {
            throw new IllegalArgumentException("Public lobby ID and requester user ID must be provided");
        }
        // Retrieve lobby
        LobbyEntity lobby = findAndLockLobby(publicLobbyId);
        // Validate lobby state
        validateLobbyForJoining(lobby, publicLobbyId);
        // Add user to lobby
        UserEntity user = userService.findUserById(userId);
        addPlayerToLobby(lobby, user);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Overloaded method, used to join private lobby when token is provided
    @Transactional
    public LobbyDto joinLobby(Long userId, String token) {
        // Validate input parameter
        if (token == null || userId == null) {
            throw new IllegalArgumentException("Private lobby token and requester user ID must be provided");
        }
        // Determine lobby ID and retrieve lobby
        Long lobbyId = resolvePrivateLobbyId(token);
        LobbyEntity lobby = findAndLockLobby(lobbyId);
        // Validate lobby state
        validateLobbyForJoining(lobby, lobbyId);
        // Add user to lobby
        UserEntity user = userService.findUserById(userId);
        addPlayerToLobby(lobby, user);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Using private lobby token, retrieve the ID of the associated private lobby
    private Long resolvePrivateLobbyId(String token) {
        return privateLobbyTokenService.joinPrivateLobbyWithToken(token);
    }

    // Get lobby and lock it from concurrent editing
    public LobbyEntity findAndLockLobby(Long lobbyId) {
        return lobbyRepository.findByIdForUpdate(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby with ID " + lobbyId + " does not exist"));
    }

    // Ensure when attempting to join lobby that it is active and not full
    private void validateLobbyForJoining(LobbyEntity lobby, Long lobbyId) {
        if (!lobby.getIsActive()) {
            throw new LobbyInactiveException("Lobby with ID " + lobbyId + " is no longer active");
        }
        if (lobby.getLobbyPlayers().size() >= 4) {
            throw new LobbyFullException("Lobby with ID " + lobbyId + " is currently full");
        }
    }

    // Create a LobbyPlayer entity instance and add them to the lobby when joining
    private void addPlayerToLobby(LobbyEntity lobby, UserEntity user) {
        LobbyPlayerEntity lobbyPlayer = LobbyPlayerFactory.createLobbyPlayer(lobby, user);
        lobby.getLobbyPlayers().add(lobbyPlayer);
    }

    @Transactional
    public LobbyDto updateLobbyDifficulty(Long lobbyId, Difficulty difficulty) {
        // Retrieve lobby
        LobbyEntity lobby = findAndLockLobby(lobbyId);
        lobby.setDifficulty(difficulty);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    @Transactional
    public LobbyDto updateLobbyTimeLimit(Long lobbyId, TimeLimitPreset timeLimit) {
        // Retrieve lobby
        LobbyEntity lobby = findAndLockLobby(lobbyId);
        lobby.setTimeLimit(timeLimit);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    @Transactional
    public LobbyDto updateLobbyPlayerStatus(Long lobbyId, Long userId, LobbyStatus lobbyStatus) {
        // Retrieve lobby
        LobbyEntity lobby = findAndLockLobby(lobbyId);
        LobbyPlayerEntity lobbyPlayer = lobby.getLobbyPlayers().stream()
                .filter(lp -> lp.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() ->
                        new LobbyPlayerNotFoundException(
                                "Lobby Player with Lobby ID " + lobbyId + " and User ID " + userId + " does not exist"
                        )
                );
        // Lobby Player managed by JPA therefore update will apply
        lobbyPlayer.setStatus(lobbyStatus);
        // Handle any countdown updates that may be required
        lobby.evaluateCountdownState();
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Get LobbyPlayerEntity using Composite ID comprised of lobbyId and userId
    private LobbyPlayerEntity findLobbyPlayerByCompositeId(Long lobbyId, Long userId) {
        return lobbyPlayerRepository.findByCompositeId(lobbyId, userId)
                .orElseThrow(() -> new LobbyPlayerNotFoundException("Lobby Player with Lobby ID " + lobbyId + " and User ID " + userId + " does not exist"));
    }

    @Transactional
    // Removes a player from the lobby either due to disconnecting or manual leave, re-orders host if the host left
    public LobbyDto removeFromLobby(Long lobbyId, Long userId) {
        // Retrieve lobby
        LobbyEntity lobby = findAndLockLobby(lobbyId);
        // Retrieve LobbyPlayer
        LobbyPlayerEntity lobbyPlayer = findLobbyPlayerByCompositeId(lobbyId, userId);
        UserEntity requesterEntity = userService.findUserById(userId);
        // If host has left, update the host to the player that joined first after the host
        if (requesterEntity.equals(lobby.getHost())) {
            Optional<UserEntity> newHostOptional = getNewHost(lobby);
            // Current host was the only active player in lobby - close down lobby
            if (newHostOptional.isEmpty()) {
                closeLobby(lobby);
                return null;
            } else {
                UserEntity newHost = newHostOptional.get();
                // Update the lobby host
                lobby.setHost(newHost);
            }
        }
        Set<LobbyPlayerEntity> activePlayers = lobby.getLobbyPlayers();
        activePlayers.remove(lobbyPlayer);
        // Delete the LobbyPlayer from the DB
        lobbyPlayerRepository.deleteByCompositeId(lobbyId, userId);
        lobby.setLobbyPlayers(activePlayers);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Return the entity record of the new host based on the order in which players joined
    private Optional<UserEntity> getNewHost(LobbyEntity lobby) {
        UserEntity currentHost = lobby.getHost();
        Set<LobbyPlayerEntity> activePlayers = lobby.getLobbyPlayers();
        return activePlayers.stream()
                // Remove current host from the stream
                .filter(lp -> !lp.getUser().equals(currentHost))
                // Order by join date/time from first to last
                .sorted(Comparator.comparing(LobbyPlayerEntity::getJoinedAt))
                // Transform to list of User entities
                .map(LobbyPlayerEntity::getUser)
                // Return the player who joined first to be the new host
                .findFirst();
    }

    @Transactional
    // Register the Lobby inactive
    public void closeLobby(LobbyEntity lobby) {
        lobby.setIsActive(false);
        // Delete the lobby from the DB, removing all associated lobby messages, games, game states
        lobbyRepository.deleteById(lobby.getId());
    }

    // Return the requested lobby
    public LobbyDto getLobbyById(Long lobbyId) {
        Optional<LobbyEntity> lobby = lobbyRepository.findById(lobbyId);
        if (lobby.isPresent()) {
            return lobby.map(lobbyEntityDtoMapper::mapToDto).orElse(null);
        } else {
            throw new LobbyNotFoundException("Lobby with ID " + lobbyId + " does not exist");
        }
    }

}
