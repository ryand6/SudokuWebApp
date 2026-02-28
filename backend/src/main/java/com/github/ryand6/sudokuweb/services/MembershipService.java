package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.repositories.GameStateRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MembershipService {
    private final GameStateRepository gameStateRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public MembershipService(GameStateRepository gameStateRepository,
                             LobbyPlayerRepository lobbyPlayerRepository) {
        this.gameStateRepository = gameStateRepository;
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
        if (!gameStateRepository.existsByUserEntityIdAndGameEntityId(userId, gameId)) {
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
}
