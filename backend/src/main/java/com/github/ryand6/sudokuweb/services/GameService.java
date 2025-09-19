package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.repositories.GameStateRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameStateRepository gameStateRepository;

    public GameService(GameStateRepository gameStateRepository) {
        this.gameStateRepository = gameStateRepository;
    }

    // Confirm if a user is a member of a game
    public boolean isUserInGame(Long userId, Long gameId) {
        return gameStateRepository.existsByUserEntityIdAndGameEntityId(userId, gameId);
    }

}
