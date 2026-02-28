package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.dto.events.PlayerHighlightedCellDto;
import com.github.ryand6.sudokuweb.dto.events.SudokuCellCoordinatesDto;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.util.GameUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameInMemoryStateService {

    private final MembershipService membershipService;

    public GameInMemoryStateService(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    private final Map<Long, Map<Long, SudokuCellCoordinatesDto>> gameHighlightedCells = new ConcurrentHashMap<>();

    public PlayerHighlightedCellDto updatePlayerHighlightedCell(PlayerHighlightedCellDto updateRequest) {
        Long gameId = updateRequest.getGameId();
        Long userId = updateRequest.getUserId();
        int row = updateRequest.getRow();
        int col = updateRequest.getCol();
        validateHighlightedCellUpdate(gameId, userId, row, col);
        gameHighlightedCells.computeIfAbsent(gameId, id -> new ConcurrentHashMap<>()).put(userId, new SudokuCellCoordinatesDto(updateRequest.getRow(), updateRequest.getCol()));
        return updateRequest;
    }

    public void removeGame(Long gameId) {
        gameHighlightedCells.remove(gameId);
    }

    public void removeGamePlayer(Long gameId, Long userId) {
        Map<Long, SudokuCellCoordinatesDto> players = gameHighlightedCells.get(gameId);
        if (players != null) {
            players.remove(userId);
            if (players.isEmpty()) {
                removeGame(gameId);
            }
        }
    }

    private void validateHighlightedCellUpdate(Long gameId, Long userId, int row, int col) {
        GameUtils.validateCellCoordinates(row, col);
        if (!membershipService.isUserInGame(userId, gameId)) {
            throw new GamePlayerNotFoundException("User with ID " + userId + " is not part of the game with ID " + gameId);
        }
    }

}
