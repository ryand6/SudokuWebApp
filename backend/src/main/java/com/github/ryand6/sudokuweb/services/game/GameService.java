package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.game.GameFactory;
import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.exceptions.game.GameCreationInterruptedException;
import com.github.ryand6.sudokuweb.exceptions.game.GameNotFoundException;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GameEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.game.GameRepository;
import com.github.ryand6.sudokuweb.services.MembershipService;
import com.github.ryand6.sudokuweb.services.puzzle.SudokuPuzzleService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyWebSocketsService;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SudokuPuzzleService sudokuPuzzleService;
    private final GameEntityDtoMapper gameEntityDtoMapper;
    private final LobbyWebSocketsService lobbyWebSocketsService;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final MembershipService membershipService;
    private final GameInMemoryStateService gameInMemoryStateService;

    public GameService(GameRepository gameRepository,
                       SudokuPuzzleService sudokuPuzzleService,
                       GameEntityDtoMapper gameEntityDtoMapper,
                       LobbyWebSocketsService lobbyWebSocketsService,
                       LobbyEntityDtoMapper lobbyEntityDtoMapper,
                       SimpMessagingTemplate messagingTemplate,
                       MembershipService membershipService,
                       GameInMemoryStateService gameInMemoryStateService) {
        this.gameRepository = gameRepository;
        this.sudokuPuzzleService = sudokuPuzzleService;
        this.gameEntityDtoMapper = gameEntityDtoMapper;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.messagingTemplate = messagingTemplate;
        this.membershipService = membershipService;
        this.gameInMemoryStateService = gameInMemoryStateService;
    }

    @Transactional
    public GameDto createGameIfNoneActive(LobbyEntity lobby) {

        // Exit task if scheduler cancels it
        if (Thread.currentThread().isInterrupted()) {
            throw new GameCreationInterruptedException("Game creation task for Lobby with ID " + lobby.getId() + " interrupted before starting DB operation");
        }

        if (lobby.isInGame()) {
            return null;
        }
        GameDto game = createGame(lobby);
        lobby.setInGame(true);
        lobby.setCurrentGameId(game.getId());
        lobby.getLobbyCountdownEntity().resetCountdownIfActive();

        // Emit notification of lobby update
        lobbyWebSocketsService.handleLobbyUpdate(lobbyEntityDtoMapper.mapToDto(lobby), messagingTemplate);

        return game;
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity - Transactional applied as multiple entities are
    saved to DB */
    @Transactional
    private GameDto createGame(LobbyEntity lobby) {

        Difficulty difficulty = lobby.getLobbySettingsEntity().getDifficulty();

        // Retrieve all active users in the game
        Set<LobbyPlayerEntity> activeLobbyPlayers = lobby.getLobbyPlayers();

        lobby.validateGameCreation();

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

    public GameDto removeGamePlayer(Long gameId, Long userId) {
        // IMPLEMENT LOGIC

        // update caches
        membershipService.removeGamePlayer(gameId, userId);
        gameInMemoryStateService.removeGamePlayer(gameId, userId);

        // CHANGE
        return new GameDto();
    }

    public GameDto endGame(Long gameId) {
        // IMPLEMENT LOGIC

        // update caches
        membershipService.removeGame(gameId);
        gameInMemoryStateService.removeGame(gameId);

        // CHANGE
        return new GameDto();
    }

}
