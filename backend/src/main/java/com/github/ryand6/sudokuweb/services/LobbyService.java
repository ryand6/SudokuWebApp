package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.domain.factory.LobbyPlayerFactory;
import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyFullException;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyInactiveException;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.UserExistsInActiveLobbyException;
import com.github.ryand6.sudokuweb.exceptions.lobby.player.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.settings.LobbySettingsLockedException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyCountdownRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.LobbySettingsRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.ryand6.sudokuweb.domain.LobbyEntity.LOBBY_SIZE;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserService userService;
    private final LobbyChatService lobbyChatService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final PrivateLobbyTokenService privateLobbyTokenService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public LobbyService(LobbyRepository lobbyRepository,
                        UserService userService,
                        LobbyChatService lobbyChatService,
                        LobbyWebSocketsService lobbyWebSocketsService,
                        LobbyEntityDtoMapper lobbyEntityDtoMapper,
                        PrivateLobbyTokenService privateLobbyTokenService,
                        SimpMessagingTemplate simpMessagingTemplate) {
        this.lobbyRepository = lobbyRepository;
        this.userService = userService;
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.privateLobbyTokenService = privateLobbyTokenService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Transactional
    public LobbyDto createNewLobby(String lobbyName, Boolean isPublic, Long requesterId) {
        Optional<LobbyEntity> isUserInActiveLobby = lobbyRepository.findFirstByIsActiveTrueAndLobbyPlayers_User_Id(requesterId);
        if (isUserInActiveLobby.isPresent()) {
            throw new UserExistsInActiveLobbyException("You are currently a player in an active lobby with ID " + isUserInActiveLobby.get().getId() + ". Players can only be in one active lobby at a time.");
        }

        LobbyEntity newLobby = new LobbyEntity();
        UserEntity requester = userService.findUserById(requesterId);
        newLobby.setLobbyName(lobbyName);
        // requester of lobby creation becomes the host
        newLobby.setHost(requester);

        LobbySettingsEntity lobbySettings = new LobbySettingsEntity();
        lobbySettings.setPublic(isPublic);
        newLobby.setLobbySettingsEntity(lobbySettings);
        lobbySettings.setLobbyEntity(newLobby);

        LobbyCountdownEntity lobbyCountdown = new LobbyCountdownEntity();
        newLobby.setLobbyCountdownEntity(lobbyCountdown);
        lobbyCountdown.setLobbyEntity(newLobby);

        newLobby.setActive(true);
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
        Optional<LobbyEntity> isUserInActiveLobby = lobbyRepository.findFirstByIsActiveTrueAndLobbyPlayers_User_Id(userId);
        if (isUserInActiveLobby.isPresent()) {
            throw new UserExistsInActiveLobbyException("You are currently a player in an active lobby with ID " + isUserInActiveLobby.get().getId() + ". Players can only be in one active lobby at a time.");
        }
        // ADD RETRY LOGIC
        LobbyEntity lobby = getLobbyById(publicLobbyId);
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
        Optional<LobbyEntity> isUserInActiveLobby = lobbyRepository.findFirstByIsActiveTrueAndLobbyPlayers_User_Id(userId);
        if (isUserInActiveLobby.isPresent()) {
            throw new UserExistsInActiveLobbyException("You are currently a player in an active lobby with ID " + isUserInActiveLobby.get().getId() + ". Players can only be in one active lobby at a time.");
        }
        // Determine lobby ID and retrieve lobby
        Long lobbyId = resolvePrivateLobbyId(token);
        // ADD RETRY LOGIC
        LobbyEntity lobby = getLobbyById(lobbyId);
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

//    // Get lobby and lock it from concurrent editing
//    public LobbyEntity findAndLockLobby(Long lobbyId) {
//        return lobbyRepository.findByIdForUpdate(lobbyId)
//                .orElseThrow(() -> new LobbyNotFoundException("Lobby with ID " + lobbyId + " does not exist"));
//    }

    // Ensure when attempting to join lobby that it is active and not full
    private void validateLobbyForJoining(LobbyEntity lobby, Long lobbyId) {
        if (!lobby.isActive()) {
            throw new LobbyInactiveException("Lobby with ID " + lobbyId + " is no longer active");
        }
        if (lobby.getLobbyPlayers().size() >= LOBBY_SIZE) {
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
        LobbyCountdownEntity lobbyCountdownEntity = lobbyC
        if (lobby.getLobbyCountdownEntity().isCountdownActive()) {
            throw new LobbySettingsLockedException("Cannot update settings whilst the countdown is active.");
        }
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.setDifficulty(difficulty);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    @Transactional
    public LobbyDto updateLobbyTimeLimit(Long lobbyId, TimeLimitPreset timeLimit) {
        LobbyEntity lobby = getLobbyById(lobbyId);
        if (lobby.getLobbyCountdownEntity().isCountdownActive()) {
            throw new LobbySettingsLockedException("Cannot update settings whilst the countdown is active.");
        }
        LobbySettingsEntity lobbySettings = lobby.getLobbySettingsEntity();
        lobbySettings.setTimeLimit(timeLimit);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    @Transactional
    public LobbyDto updateLobbyPlayerStatus(Long lobbyId, Long userId, LobbyStatus lobbyStatus) {
        LobbyEntity lobby = getLobbyById(lobbyId);
        LobbyPlayerEntity lobbyPlayer = findLobbyPlayer(lobby, userId);
        // Lobby Player managed by JPA therefore update will apply
        lobbyPlayer.setStatus(lobbyStatus);
        // Handle any countdown updates that may be required
        Optional<Long> newInitiator = lobby.getLobbyCountdownEntity().evaluateCountdownState();
        newInitiator.ifPresent(id -> {
            LobbyChatMessageDto infoMessage = lobbyChatService.submitInfoMessage(lobbyId, id, "started the new game countdown.");
            lobbyWebSocketsService.handleLobbyChatMessage(infoMessage, simpMessagingTemplate);
        });
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    @Transactional
    // Removes a player from the lobby either due to disconnecting or manual leave, re-orders host if the host left
    public LobbyDto removeFromLobby(Long lobbyId, Long userId) {
        // ADD RETRY LOGIC
        LobbyEntity lobby = getLobbyById(lobbyId);
        // Retrieve LobbyPlayer
        LobbyPlayerEntity lobbyPlayer = findLobbyPlayer(lobby, userId);
        UserEntity requesterEntity = userService.findUserById(userId);

        // If host has left, update the host to the player that joined first after the host
        if (requesterEntity.equals(lobby.getHost())) {
            applyHostChangeOrCloseLobby(lobby);
            // If lobby has been closed
            if (!lobby.isActive()) {
                return null;
            }
        }

        // Send an info update to the lobby chat - must be sent before the transaction is committed, otherwise it will fail WebSocketSecurity checks that ensure messages come from active lobby members
        LobbyChatMessageDto infoMessage = lobbyChatService.submitInfoMessage(lobbyId, userId, "left the lobby.");
        lobbyWebSocketsService.handleLobbyChatMessage(infoMessage, simpMessagingTemplate);

        // Remove the lobby player from the lobby - hibernate will clean up by removing the lobby player from the DB due to orphan removal
        lobby.getLobbyPlayers().remove(lobbyPlayer);

        // Stop the timer if the player count goes below 2
        lobby.getLobbyCountdownEntity().evaluateCountdownState();
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    private void applyHostChangeOrCloseLobby(LobbyEntity lobby) {
        Optional<UserEntity> newHost = lobby.determineNextHost();
        if (newHost.isEmpty()) {
            closeLobby(lobby);
        } else {
            lobby.setHost(newHost.get());
        }
    }

    LobbyPlayerEntity findLobbyPlayer(LobbyEntity lobby, Long userId) {
        return lobby.getLobbyPlayers().stream()
                .filter(lp -> lp.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new LobbyPlayerNotFoundException(
                        "Lobby Player with Lobby ID " + lobby.getId() + " and User ID " + userId + " does not exist"
                ));
    }

    @Transactional
    // Register the Lobby inactive
    public void closeLobby(LobbyEntity lobby) {
        lobby.setActive(false);
        // Delete the lobby from the DB, removing all associated lobby messages, games, game states
        lobbyRepository.deleteById(lobby.getId());
    }

    public LobbyEntity getLobbyById(Long lobbyId) {
        Optional<LobbyEntity> lobby = lobbyRepository.findById(lobbyId);
        if (lobby.isPresent()) {
            return lobby.get();
        } else {
            throw new LobbyNotFoundException("Lobby with ID " + lobbyId + " does not exist");
        }
    }

    @Transactional
    // Return the requested lobby DTO
    public LobbyDto getLobbyDtoById(Long lobbyId) {
        LobbyEntity lobby = getLobbyById(lobbyId);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

}
