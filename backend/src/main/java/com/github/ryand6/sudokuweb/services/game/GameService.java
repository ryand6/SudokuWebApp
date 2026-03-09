package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerFactory;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateRepository;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyRepository;
import com.github.ryand6.sudokuweb.domain.lobby.player.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.game.GameFactory;
import com.github.ryand6.sudokuweb.dto.entity.game.GameDto;
import com.github.ryand6.sudokuweb.dto.entity.game.PrivateGamePlayerStateDto;
import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import com.github.ryand6.sudokuweb.exceptions.game.GameCreationInterruptedException;
import com.github.ryand6.sudokuweb.exceptions.game.GameNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.state.GamePlayerStateNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GameEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.game.PrivateGamePlayerStateEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.game.GameRepository;
import com.github.ryand6.sudokuweb.services.MembershipService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyCountdownMutationService;
import com.github.ryand6.sudokuweb.services.puzzle.SudokuPuzzleService;
import com.github.ryand6.sudokuweb.services.lobby.LobbyWebSocketsService;
import com.github.ryand6.sudokuweb.util.TransactionUtils;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    private final GamePlayerStateRepository gamePlayerStateRepository;
    private final PrivateGamePlayerStateEntityDtoMapper privateGamePlayerStateEntityDtoMapper;
    private final LobbyRepository lobbyRepository;
    private final LobbyCountdownMutationService lobbyCountdownMutationService;

    public GameService(GameRepository gameRepository,
                       SudokuPuzzleService sudokuPuzzleService,
                       GameEntityDtoMapper gameEntityDtoMapper,
                       LobbyWebSocketsService lobbyWebSocketsService,
                       LobbyEntityDtoMapper lobbyEntityDtoMapper,
                       SimpMessagingTemplate messagingTemplate,
                       MembershipService membershipService,
                       GameInMemoryStateService gameInMemoryStateService,
                       GamePlayerStateRepository gamePlayerStateRepository,
                       PrivateGamePlayerStateEntityDtoMapper privateGamePlayerStateEntityDtoMapper,
                       LobbyRepository lobbyRepository,
                       LobbyCountdownMutationService lobbyCountdownMutationService) {
        this.gameRepository = gameRepository;
        this.sudokuPuzzleService = sudokuPuzzleService;
        this.gameEntityDtoMapper = gameEntityDtoMapper;
        this.lobbyWebSocketsService = lobbyWebSocketsService;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.messagingTemplate = messagingTemplate;
        this.membershipService = membershipService;
        this.gameInMemoryStateService = gameInMemoryStateService;
        this.gamePlayerStateRepository = gamePlayerStateRepository;
        this.privateGamePlayerStateEntityDtoMapper = privateGamePlayerStateEntityDtoMapper;
        this.lobbyRepository = lobbyRepository;
        this.lobbyCountdownMutationService = lobbyCountdownMutationService;
    }

    @Transactional
    public GameDto createGameIfNoneActive(Long lobbyId) {
        // Exit task if scheduler cancels it
        if (Thread.currentThread().isInterrupted()) {
            throw new GameCreationInterruptedException("Game creation task for Lobby with ID " + lobbyId + " interrupted before starting DB operation");
        }
        // Use repository to get lobby; using LobbyService creates a circular dependency
        LobbyEntity lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException("Lobby with ID " + lobbyId + " does not exist"));

        if (lobby.isInGame()) {
            return null;
        }

        GameDto game = createGame(lobby);
        lobby.setInGame(true);
        lobby.setCurrentGameId(game.getGameId());

        lobbyCountdownMutationService.safeCountdownReset(lobby.getLobbyCountdownEntity());

        // Emit notification of lobby update after transaction committed
        TransactionUtils.run(() -> {
            lobbyWebSocketsService.handleLobbyUpdate(lobbyEntityDtoMapper.mapToDto(lobby), messagingTemplate);
        });


        return game;
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity - Transactional applied as multiple entities are
    saved to DB */
    @Transactional
    private GameDto createGame(LobbyEntity lobby) {

        Difficulty difficulty = lobby.getLobbySettingsEntity().getDifficulty();

        lobby.validateGameCreation();

        // Call static method to generate sudokuPuzzleEntity, retrieving both the sudokuPuzzleEntity and solution as a string
        // interpretation of a nested int array
        SudokuPuzzleEntity sudokuPuzzle = sudokuPuzzleService.generatePuzzle(difficulty);

        GameEntity newGame = GameFactory.createGame(lobby, sudokuPuzzle);

        gameRepository.saveAndFlush(newGame);

        Set<GamePlayerEntity> gamePlayers = newGame.getGamePlayerEntities();

        List<PlayerColour> colourList = newGame.getShuffledPlayerColours();

        int i = 0;

        // Create Game Players
        for (LobbyPlayerEntity lobbyPlayerEntity : lobby.getLobbyPlayers()) {
            PlayerColour playerColour = colourList.get(i);
            GamePlayerEntity gamePlayer = GamePlayerFactory.createGamePlayer(newGame, lobbyPlayerEntity.getUser(), playerColour, newGame.isGameStateShared(), newGame.getSudokuPuzzleEntity().getInitialBoardState());
            gamePlayers.add(gamePlayer);
            i++;
        }

        return gameEntityDtoMapper.mapToDto(newGame);
    }

    public GameDto getGameById(Long gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException("Game with ID " + gameId + " does not exist"));
        return gameEntityDtoMapper.mapToDto(gameEntity);
    }

    public PrivateGamePlayerStateDto getGamePlayerState(Long gameId, Long userId) {
        GamePlayerStateEntity gamePlayerState = gamePlayerStateRepository.findByCompositeId(gameId, userId)
                .orElseThrow(
                        () -> new GamePlayerStateNotFoundException("Game player with game ID " + gameId + " and user ID " + userId + " does not exist")
                );
        return privateGamePlayerStateEntityDtoMapper.mapToDto(gamePlayerState);
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
