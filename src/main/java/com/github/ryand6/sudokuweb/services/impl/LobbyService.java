package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.domain.factory.LobbyPlayerFactory;
import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.exceptions.*;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.util.SecureInvitationsUtil;
import jakarta.annotation.Nullable;
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

    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 25;

    public LobbyService(LobbyRepository lobbyRepository,
                        UserService userService,
                        LobbyEntityDtoMapper lobbyEntityDtoMapper,
                        LobbyPlayerRepository lobbyPlayerRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userService = userService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
    }

    @Transactional
    public LobbyDto createNewLobby(String lobbyName, Boolean isPublic, Boolean isPrivate, Long requesterId) {
        LobbyEntity newLobby = new LobbyEntity();
        // One of these must be true or there is an error with the parameters
        if (Boolean.TRUE.equals(isPublic)) {
            newLobby.setIsPublic(true);
        } else if (Boolean.TRUE.equals(isPrivate)) {
            newLobby.setIsPublic(false);
        } else {
            throw new InvalidLobbyPublicStatusParametersException("Either Public or Private lobby must be checked");
        }
        UserEntity requester = userService.findUserById(requesterId);
        newLobby.setLobbyName(lobbyName);
        // requester of lobby creation becomes the host
        newLobby.setHost(requester);
        newLobby.setIsActive(true);
        // Save the lobby first so that it can then be referenced by the LobbyPlayerEntity to be attached to the new lobby
        lobbyRepository.saveAndFlush(newLobby);
        // Create a set of users only containing the requester for now, until other users join the lobby
        Set<LobbyPlayerEntity> lobbyPlayers = new HashSet<>();
        // Create LobbyPlayerEntity for requester
        LobbyPlayerEntity lobbyPlayerRequester = LobbyPlayerFactory.createLobbyPlayer(newLobby, requester);
        lobbyPlayerRepository.save(lobbyPlayerRequester);
        lobbyPlayers.add(lobbyPlayerRequester);
        newLobby.setLobbyPlayers(lobbyPlayers);
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

    // Attempts to add user to a lobby, checking first to see if the lobby is both active and whether it's currently full (4 players)
    @Transactional
    public LobbyDto joinLobby(@Nullable Long publicLobbyId, Long userId, @Nullable String token) {
        // Validate input parameters
        if (publicLobbyId == null && token == null) {
            throw new IllegalArgumentException("Either publicLobbyId or token must be provided");
        }
        if (publicLobbyId != null && token != null) {
            throw new IllegalArgumentException("Cannot provide both publicLobbyId and token");
        }

        // Determine lobby ID and retrieve lobby
        Long lobbyId = resolveLobbyId(publicLobbyId, token);
        LobbyEntity lobby = findAndLockLobby(lobbyId);

        // Validate lobby state
        validateLobbyForJoining(lobby, lobbyId);

        // Add user to lobby
        UserEntity user = userService.findUserById(userId);
        addPlayerToLobby(lobby, user);

        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    private Long resolveLobbyId(@Nullable Long publicLobbyId, @Nullable String token) {
        if (publicLobbyId != null) {
            return publicLobbyId;
        }
        Long privateLobbyId = SecureInvitationsUtil.validateInvitationToken(token);
        if (privateLobbyId == null) {
            throw new InvalidTokenException("Invalid or expired invitation token");
        }
        return privateLobbyId;
    }

    private LobbyEntity findAndLockLobby(Long lobbyId) {
        return lobbyRepository.findByIdForUpdate(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby with ID " + lobbyId + " does not exist"));
    }

    private void validateLobbyForJoining(LobbyEntity lobby, Long lobbyId) {
        if (!lobby.getIsActive()) {
            throw new LobbyInactiveException("Lobby with ID " + lobbyId + " is no longer active");
        }
        if (lobby.getLobbyPlayers().size() >= 4) {
            throw new LobbyFullException("Lobby with ID " + lobbyId + " is currently full");
        }
    }

    private void addPlayerToLobby(LobbyEntity lobby, UserEntity user) {
        LobbyPlayerEntity lobbyPlayer = LobbyPlayerFactory.createLobbyPlayer(lobby, user);
        lobby.getLobbyPlayers().add(lobbyPlayer);
    }

    @Transactional
    // Removes a player from the lobby either due to disconnecting or manual leave, re-orders host if the host left
    public LobbyDto removeFromLobby(Long userId, Long lobbyId) {
        // Try retrieve lobby and lock for editing, preventing race conditions
        Optional<LobbyEntity> lobbyOptional = lobbyRepository.findByIdForUpdate(lobbyId);
        if (lobbyOptional.isEmpty()) {
            throw new LobbyNotFoundException("Lobby with ID " + lobbyId + " does not exist");
        }
        LobbyEntity lobby = lobbyOptional.get();
        // Try retrieve the LobbyPlayer associated with the user leaving
        Optional<LobbyPlayerEntity> lobbyPlayerRequesterOptional = lobbyPlayerRepository.findByCompositeId(lobbyId, userId);
        if (lobbyPlayerRequesterOptional.isEmpty()) {
            throw new LobbyPlayerNotFoundException("Lobby Player with Lobby ID " + lobbyId + " and User ID " + userId + " does not exist");
        }
        UserEntity requesterEntity = userService.findUserById(userId);
        // If host has left, update the host to the player that joined first after the host
        if (requesterEntity.equals(lobby.getHost())) {
            Optional<UserEntity> newHostOptional = getNewHost(lobby);
            // Current host was the only active player in lobby - close down lobby
            if (newHostOptional.isEmpty()) {
                lobby = closeLobby(lobby);
            } else {
                UserEntity newHost = newHostOptional.get();
                // Update the lobby host
                lobby.setHost(newHost);
            }
        }
        Set<LobbyPlayerEntity> activePlayers = lobby.getLobbyPlayers();
        LobbyPlayerEntity lobbyPlayerRequester = lobbyPlayerRequesterOptional.get();
        activePlayers.remove(lobbyPlayerRequester);
        // Delete the LobbyPlayer from the DB
        lobbyPlayerRepository.deleteByCompositeId(lobbyId, userId);
        lobby.setLobbyPlayers(activePlayers);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Return the entity record of the new host based on the order in which players joined
    public Optional<UserEntity> getNewHost(LobbyEntity lobby) {
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

    // Register the Lobby inactive
    public LobbyEntity closeLobby(LobbyEntity lobby) {
        lobby.setIsActive(false);
        return lobby;
    }

    // Return the requested lobby
    public LobbyDto getLobbyById(Long lobbyId) {
        Optional<LobbyEntity> lobby = lobbyRepository.findById(lobbyId);
        return lobby.map(lobbyEntityDtoMapper::mapToDto).orElse(null);
    }

}
