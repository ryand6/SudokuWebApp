package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.repositories.GameStateRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyPlayerRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import org.springframework.stereotype.Component;

@Component
public class MembershipCheckService {
    private final GameStateRepository gameStateRepository;
    private final LobbyPlayerRepository lobbyPlayerRepository;

    public MembershipCheckService(GameStateRepository gameStateRepository,
                                  LobbyPlayerRepository lobbyPlayerRepository) {
        this.gameStateRepository = gameStateRepository;
        this.lobbyPlayerRepository = lobbyPlayerRepository;
    }

    // Confirm if a user is a member of a game
    public boolean isUserInGame(Long userId, Long gameId) {
        return gameStateRepository.existsByUserEntityIdAndGameEntityId(userId, gameId);
    }

    // Confirm if a user is currently part of a lobby
    public boolean isUserInLobby(Long userId, Long lobbyId) {
        return lobbyPlayerRepository.existsByUserIdAndLobbyId(userId, lobbyId);
    }
}
