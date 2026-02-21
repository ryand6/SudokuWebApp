package com.github.ryand6.sudokuweb.services;

import com.github.ryand6.sudokuweb.domain.GameEntity;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.factory.GameFactory;
import com.github.ryand6.sudokuweb.dto.entity.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.exceptions.game.GameNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.TooManyActivePlayersException;
import com.github.ryand6.sudokuweb.mappers.Impl.GameEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.GameRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SudokuPuzzleService sudokuPuzzleService;
    private final GameEntityDtoMapper gameEntityDtoMapper;
    private final LobbyService lobbyService;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public GameService(GameRepository gameRepository,
                       SudokuPuzzleService sudokuPuzzleService,
                       GameEntityDtoMapper gameEntityDtoMapper,
                       LobbyService lobbyService,
                       LobbyWebSocketsService lobbyWebSocketsService,
                       LobbyEntityDtoMapper lobbyEntityDtoMapper,
                       SimpMessagingTemplate messagingTemplate) {
        this.gameRepository = gameRepository;
        this.sudokuPuzzleService = sudokuPuzzleService;
        this.gameEntityDtoMapper = gameEntityDtoMapper;
        this.lobbyService = lobbyService;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public GameDto createGameIfNoneActive(LobbyDto lobby) {
        LobbyEntity lockedLobby = lobbyService.findAndLockLobby(lobby.getId());

        if (lockedLobby.getInGame()) {
            return null;
        }
        GameDto game = createGame(lockedLobby);
        lockedLobby.setInGame(true);
        lockedLobby.setCurrentGameId(game.getId());
        lockedLobby.resetCountdownIfActive();

        // Emit notification of lobby update
        lobbyWebSocketsService.handleLobbyUpdate(lobbyEntityDtoMapper.mapToDto(lockedLobby), messagingTemplate);

        System.out.println("\n\n\n" + game.toString() + "\n\n\n");

        return game;
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity - Transactional applied as multiple entities are
    saved to DB */
    @Transactional
    private GameDto createGame(LobbyEntity lobby) {

        Difficulty difficulty = lobby.getDifficulty();

        // Retrieve all active users in the game
        Set<LobbyPlayerEntity> activeLobbyPlayers = lobby.getLobbyPlayers();

        if (activeLobbyPlayers.size() > 4) {
            throw new TooManyActivePlayersException("Cannot create game: Lobby with id " + lobby.getId() + " has more than 4 active players.");
        }

        // Call static method to generate sudokuPuzzleEntity, retrieving both the sudokuPuzzleEntity and solution as a string
        // interpretation of a nested int array
        SudokuPuzzleEntity sudokuPuzzle = sudokuPuzzleService.generatePuzzle(difficulty);

        GameEntity newGame = GameFactory.createGame(lobby, sudokuPuzzle);
        gameRepository.save(newGame);

        return gameEntityDtoMapper.mapToDto(newGame);
    }

    public GameDto getGameById(Long gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException("Game with ID " + gameId + " does not exist"));
        return gameEntityDtoMapper.mapToDto(gameEntity);
    }

}
