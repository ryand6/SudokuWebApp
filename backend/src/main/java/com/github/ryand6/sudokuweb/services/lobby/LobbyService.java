package com.github.ryand6.sudokuweb.services.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.countdown.CountdownEvaluationResult;
import com.github.ryand6.sudokuweb.domain.lobby.countdown.LobbyCountdownEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerFactory;
import com.github.ryand6.sudokuweb.domain.lobby.*;
import com.github.ryand6.sudokuweb.domain.lobby.settings.LobbySettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.LobbyChatMessageDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.exceptions.lobby.*;
import com.github.ryand6.sudokuweb.exceptions.lobby.player.LobbyPlayerNotFoundException;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;
import com.github.ryand6.sudokuweb.services.MembershipService;
import com.github.ryand6.sudokuweb.services.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity.LOBBY_SIZE;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserService userService;
    private final LobbyChatService lobbyChatService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final PrivateLobbyTokenService privateLobbyTokenService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MembershipService membershipService;
    private final LobbyCountdownService lobbyCountdownService;

    public LobbyService(LobbyRepository lobbyRepository,
                        UserService userService,
                        LobbyChatService lobbyChatService,
                        LobbyWebSocketsService lobbyWebSocketsService,
                        LobbyEntityDtoMapper lobbyEntityDtoMapper,
                        PrivateLobbyTokenService privateLobbyTokenService,
                        SimpMessagingTemplate simpMessagingTemplate,
                        MembershipService membershipService,
                        LobbyCountdownService lobbyCountdownService) {
        this.lobbyRepository = lobbyRepository;
        this.userService = userService;
        this.lobbyChatService = lobbyChatService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.privateLobbyTokenService = privateLobbyTokenService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.membershipService = membershipService;
        this.lobbyCountdownService = lobbyCountdownService;
    }

    @Transactional
    public LobbyDto createNewLobby(String lobbyName, Boolean isPublic, Long requesterId) {
        Set<LobbyEntity> activeLobbies = lobbyRepository.findByLobbyPlayers_User_IdAndIsActiveTrue(requesterId);
        if (!activeLobbies.isEmpty()) {
            throw new UserExistsInActiveLobbyException("You are currently a player in an active lobby called " + activeLobbies.iterator().next().getLobbyName() + ". Players can only be in one active lobby at a time.");
        }

        LobbyEntity newLobby = new LobbyEntity();
        newLobby.setActive(true);

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

        // Save the lobby first so that it can then be referenced by the LobbyPlayerEntity to be attached to the new lobby
        lobbyRepository.saveAndFlush(newLobby);
        addPlayerToLobby(newLobby, requester);
        return lobbyEntityDtoMapper.mapToDto(newLobby);
    }

    // Retrieves a page of lobbies based on the specified page number and size. Page results are ordered by createdAt (newest first)
    public List<LobbyDto> getPublicLobbies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return lobbyRepository.findByIsActiveTrueAndLobbySettingsEntity_IsPublicTrue(pageable)
                .stream()
                .map(lobbyEntityDtoMapper::mapToDto)
                .collect(Collectors.toList());
    }

    // Overloaded method, used to join public lobby when lobby ID is provided
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public LobbyDto joinLobby(Long userId, Long publicLobbyId) {
        // Validate input parameters
        if (publicLobbyId == null || userId == null) {
            throw new IllegalArgumentException("Public lobby ID and requester user ID must be provided");
        }
        Set<LobbyEntity> activeLobbies = lobbyRepository.findByLobbyPlayers_User_IdAndIsActiveTrue(userId);
        if (!activeLobbies.isEmpty()) {
            throw new UserExistsInActiveLobbyException("You are currently a player in an active lobby called " + activeLobbies.iterator().next().getLobbyName() + ". Players can only be in one active lobby at a time.");
        }
        LobbyEntity lobby = getLobbyById(publicLobbyId);
        lobby.validateIfPlayerCanJoin(userId);
        // Add user to lobby
        UserEntity user = userService.findUserById(userId);
        addPlayerToLobby(lobby, user);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Fallback if retries fail for join lobby
    @Recover
    public LobbyDto joinLobbyRecover(ObjectOptimisticLockingFailureException ex, Long userId, Long publicLobbyId) {
        throw new LobbyOptimisticLockException("Unable to join lobby with due to a conflict. Please try again shortly.");
    }

    // Overloaded method, used to join private lobby when token is provided
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    public LobbyDto joinLobby(Long userId, String token) {
        // Validate input parameter
        if (token == null || userId == null) {
            throw new IllegalArgumentException("Private lobby token and requester user ID must be provided");
        }
        Set<LobbyEntity> activeLobbies = lobbyRepository.findByLobbyPlayers_User_IdAndIsActiveTrue(userId);
        if (!activeLobbies.isEmpty()) {
            throw new UserExistsInActiveLobbyException("You are currently a player in an active lobby called " + activeLobbies.iterator().next().getLobbyName() + ". Players can only be in one active lobby at a time.");
        }
        // Determine lobby ID and retrieve lobby
        Long lobbyId = resolvePrivateLobbyId(token);
        LobbyEntity lobby = getLobbyById(lobbyId);
        lobby.validateIfPlayerCanJoin(userId);
        // Add user to lobby
        UserEntity user = userService.findUserById(userId);
        addPlayerToLobby(lobby, user);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Fallback if retries fail for join lobby
    @Recover
    public LobbyDto joinLobbyRecover(ObjectOptimisticLockingFailureException ex, Long userId, String token) {
        throw new LobbyOptimisticLockException("Unable to join lobby due to a conflict. Please try again shortly.");
    }

    // Using private lobby token, retrieve the ID of the associated private lobby
    private Long resolvePrivateLobbyId(String token) {
        return privateLobbyTokenService.joinPrivateLobbyWithToken(token);
    }

    // Create a LobbyPlayer entity instance and add them to the lobby when joining
    private void addPlayerToLobby(LobbyEntity lobby, UserEntity user) {
        LobbyPlayerEntity lobbyPlayer = LobbyPlayerFactory.createLobbyPlayer(lobby, user);
        lobby.getLobbyPlayers().add(lobbyPlayer);
    }

    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 50, multiplier = 2)
    )
    @Transactional
    // Removes a player from the lobby either due to disconnecting or manual leave, re-orders host if the host left
    public LobbyDto removeFromLobby(Long lobbyId, Long userId) {
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

        // Update cache
        membershipService.removeLobbyPlayer(lobbyId, userId);

        // Stop the timer if the player count goes below 2
        CountdownEvaluationResult countdownEvaluationResult = lobby.getLobbyCountdownEntity().evaluateCountdownState();

        lobbyCountdownService.handleCountdownEvaluationResult(lobby, countdownEvaluationResult);
        return lobbyEntityDtoMapper.mapToDto(lobby);
    }

    // Fallback if retries fail for leaving lobby
    @Recover
    public LobbyDto removeFromLobbyRecover(ObjectOptimisticLockingFailureException ex, Long lobbyId, Long userId) {
        throw new LobbyOptimisticLockException("Unable to leave lobby due to a conflict. Please try again shortly.");
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

        // CONSIDER WHETHER TO DELETE OR SOFT DELETE - want to keep game history
        //lobbyRepository.deleteById(lobby.getId());

        // Update cache
        membershipService.removeLobby(lobby.getId());
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
