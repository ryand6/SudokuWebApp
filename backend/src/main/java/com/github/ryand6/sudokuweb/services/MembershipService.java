package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerRepository;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.events.types.game.GameClosedEvent;
import com.github.ryand6.sudokuweb.events.types.game.GamePlayerLeftEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyLeftMembershipEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyPlayerLeftMembershipEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MembershipService {
    private final GamePlayerRepository gamePlayerRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public MembershipService(GamePlayerRepository gamePlayerRepository,
                             LobbyPlayerRepository lobbyPlayerRepository) {
        this.gamePlayerRepository = gamePlayerRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
    }

    private final Map<Long, Set<Long>> gamePlayers = new ConcurrentHashMap<>();

    private final Map<Long, Set<Long>> lobbyPlayers = new ConcurrentHashMap<>();

    public boolean isUserInGame(Long userId, Long gameId) {
        Set<Long> players = gamePlayers.get(gameId);

        if (players != null && players.contains(userId)) {
            return true;
        }

        // Fallback to DB if player not found in cache
        if (!gamePlayerRepository.existsByUserEntity_IdAndGameEntity_Id(userId, gameId)) {
            return false;
        }

        // Add the game player to the cache if exists in DB
        gamePlayers.computeIfAbsent(gameId, id -> ConcurrentHashMap.newKeySet()).add(userId);
        return true;
    }

    // Call when game ends
    public void removeGame(Long gameId) {
        gamePlayers.remove(gameId);
    }

    // Call when a player leaves a game
    public void removeGamePlayer(Long gameId, Long userId) {
        Set<Long> players = gamePlayers.get(gameId);
        if (players != null) {
            players.remove(userId);
            if (players.isEmpty()) {
                removeGame(gameId);
            }
        }
    }

    // Confirm if a user is currently part of a lobby
    public boolean isUserInLobby(Long userId, Long lobbyId) {
        Set<Long> players = lobbyPlayers.get(lobbyId);

        if (players != null && players.contains(userId)) {
            return true;
        }

        // Fallback to DB if player not found in cache
        if (!lobbyPlayerRepository.existsByUser_IdAndLobby_Id(userId, lobbyId)) {
            return false;
        }

        lobbyPlayers.computeIfAbsent(lobbyId, id -> ConcurrentHashMap.newKeySet()).add(userId);
        return true;
    }

    // Call when a player leaves a lobby
    public void removeLobbyPlayer(Long lobbyId, Long userId) {
        Set<Long> players = lobbyPlayers.get(lobbyId);
        if (players != null) {
            players.remove(userId);
            if (players.isEmpty()) {
                removeLobby(lobbyId);
            }
        }
    }

    public void removeLobby(Long lobbyId) {
        lobbyPlayers.remove(lobbyId);
    }

    @EventListener
    void handleLobbyPlayerLeftMembershipEvent(LobbyPlayerLeftMembershipEvent event) {
        removeLobbyPlayer(event.getLobbyId(), event.getUserId());
    }

    @EventListener
    void handleLobbyLeftMembershipEvent(LobbyLeftMembershipEvent event) {
        removeLobby(event.getLobbyId());
    }

    @EventListener
    void handleGamePlayerLeftMembershipEvent(GamePlayerLeftEvent event) {
        removeGamePlayer(event.getGameId(), event.getUserId());
    }

    @EventListener
    void handleGameLeftMembershipEvent(GameClosedEvent event) {
        removeGame(event.getGameId());
    }

}
