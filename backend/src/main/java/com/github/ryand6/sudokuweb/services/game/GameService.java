package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventRequest;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventSequenceEntity;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventSequenceRepository;
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
import com.github.ryand6.sudokuweb.dto.entity.game.GamePlayerDto;
import com.github.ryand6.sudokuweb.dto.entity.game.PrivateGamePlayerStateDto;
import com.github.ryand6.sudokuweb.enums.*;
import com.github.ryand6.sudokuweb.events.types.game.CreateGameLogEvent;
import com.github.ryand6.sudokuweb.events.types.game.GameClosedEvent;
import com.github.ryand6.sudokuweb.events.types.game.GamePlayerForfeitEvent;
import com.github.ryand6.sudokuweb.events.types.game.GamePlayerLeftEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyCountdownResetEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.ws.LobbyUpdatePostGameCreationWsEvent;
import com.github.ryand6.sudokuweb.exceptions.game.GameCreationInterruptedException;
import com.github.ryand6.sudokuweb.exceptions.game.GameNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.state.GamePlayerStateNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GameEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GamePlayerEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.game.PrivateGamePlayerStateEntityDtoMapper;
import com.github.ryand6.sudokuweb.domain.game.GameRepository;
import com.github.ryand6.sudokuweb.mappers.Impl.lobby.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.services.puzzle.SudokuPuzzleService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SudokuPuzzleService sudokuPuzzleService;
    private final GameEntityDtoMapper gameEntityDtoMapper;
    private final GamePlayerEntityDtoMapper gamePlayerEntityDtoMapper;
    private final GamePlayerStateRepository gamePlayerStateRepository;
    private final PrivateGamePlayerStateEntityDtoMapper privateGamePlayerStateEntityDtoMapper;
    private final LobbyRepository lobbyRepository;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final GameEventSequenceRepository gameEventSequenceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public GameService(GameRepository gameRepository,
                       SudokuPuzzleService sudokuPuzzleService,
                       GameEntityDtoMapper gameEntityDtoMapper,
                       GamePlayerEntityDtoMapper gamePlayerEntityDtoMapper,
                       GamePlayerStateRepository gamePlayerStateRepository,
                       PrivateGamePlayerStateEntityDtoMapper privateGamePlayerStateEntityDtoMapper,
                       LobbyRepository lobbyRepository,
                       LobbyEntityDtoMapper lobbyEntityDtoMapper,
                       GameEventSequenceRepository gameEventSequenceRepository,
                       ApplicationEventPublisher applicationEventPublisher) {
        this.gameRepository = gameRepository;
        this.sudokuPuzzleService = sudokuPuzzleService;
        this.gameEntityDtoMapper = gameEntityDtoMapper;
        this.gamePlayerEntityDtoMapper = gamePlayerEntityDtoMapper;
        this.gamePlayerStateRepository = gamePlayerStateRepository;
        this.privateGamePlayerStateEntityDtoMapper = privateGamePlayerStateEntityDtoMapper;
        this.lobbyRepository = lobbyRepository;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.gameEventSequenceRepository = gameEventSequenceRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public GameDto getGameDtoById(Long gameId) {
        GameEntity gameEntity = gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException("Game with ID " + gameId + " does not exist"));
        return gameEntityDtoMapper.mapToDto(gameEntity);
    }

    public GameEntity getGameById(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException("Game with ID " + gameId + " does not exist"));
    }

    public PrivateGamePlayerStateDto getGamePlayerState(Long gameId, Long userId) {
        GamePlayerStateEntity gamePlayerState = gamePlayerStateRepository.findByCompositeId(gameId, userId)
                .orElseThrow(
                        () -> new GamePlayerStateNotFoundException("Game player with game ID " + gameId + " and user ID " + userId + " does not exist")
                );
        return privateGamePlayerStateEntityDtoMapper.mapToDto(gamePlayerState);
    }

    GamePlayerEntity findGamePlayer(GameEntity game, Long userId) {
        return game.getGamePlayerEntities().stream()
                .filter(gp -> gp.getUserEntity().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new GamePlayerNotFoundException(
                        "Game Player with Game ID " + game.getId() + " and User ID " + userId + " does not exist"
                ));
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

        lobby.handleGameCreation(game.getGameId());

        applicationEventPublisher.publishEvent(
                new LobbyCountdownResetEvent(lobby.getLobbyCountdownEntity())
        );

        // Emit notification of lobby update after transaction committed
        applicationEventPublisher.publishEvent(
                new LobbyUpdatePostGameCreationWsEvent(lobbyEntityDtoMapper.mapToDto(lobby))
        );

        return game;
    }

    /* Generate a new sudokuPuzzleEntity for the current lobby and creating lobbyState records for each
    active user in the lobby for the new sudokuPuzzleEntity - Transactional applied as multiple entities are
    saved to DB */
    @Transactional
    private GameDto createGame(LobbyEntity lobby) {

        Difficulty difficulty = lobby.getLobbySettingsEntity().getDifficulty();

        lobby.validateGameCreation();

        SudokuPuzzleEntity sudokuPuzzle = sudokuPuzzleService.generatePuzzle(difficulty);

        GameEntity newGame = GameFactory.createGame(lobby, sudokuPuzzle);

        gameRepository.saveAndFlush(newGame);

        Set<GamePlayerEntity> gamePlayers = newGame.getGamePlayerEntities();

        List<PlayerColour> colourList = newGame.getShuffledPlayerColours();

        int i = 0;

        // Create Game Players
        for (LobbyPlayerEntity lobbyPlayerEntity : lobby.getLobbyPlayers()) {
            PlayerColour playerColour = colourList.get(i);
            GamePlayerEntity gamePlayer = GamePlayerFactory.createGamePlayer(newGame, lobbyPlayerEntity.getUser(), playerColour, newGame.isBoardStateShared(), newGame.getSudokuPuzzleEntity().getInitialBoardState());
            gamePlayers.add(gamePlayer);
            i++;
        }

        // Initialise sequence entity for ordering game events
        GameEventSequenceEntity gameEventSequence = GameEventSequenceEntity.builder()
                .gameId(newGame.getId())
                .build();

        gameEventSequenceRepository.save(gameEventSequence);

        return gameEntityDtoMapper.mapToDto(newGame);
    }

    @Transactional
    public GameDto forfeitGamePlayer(Long gameId, Long userId) {
        GameEntity game = getGameById(gameId);

        GamePlayerEntity gamePlayer = findGamePlayer(game, userId);

        if (game.isAborted(gamePlayer)) {
            abortGame(game);
        }

        game.findLastRemainingOpponent(gamePlayer)
                .ifPresent(this::handlePlayerFinish);

        gamePlayer.setGameResult(GameResult.FORFEIT);

        gameRepository.save(game);

        // Send event to remove player from lobby and update membership/in memory caches
        applicationEventPublisher.publishEvent(
                new GamePlayerLeftEvent(game.getId(), game.getLobbyEntity().getId(), userId)
        );

        GameDto gameDto = gameEntityDtoMapper.mapToDto(game);
        GamePlayerDto gamePlayerDto = gamePlayerEntityDtoMapper.mapToDto(gamePlayer);

        applicationEventPublisher.publishEvent(
                new CreateGameLogEvent(gameId, userId, new GameEventRequest(GameEventType.GAME_PLAYER_FORFEIT, "player " + gamePlayerDto.getUser().getUsername() + " has forfeit and left the game"))
        );

        applicationEventPublisher.publishEvent(
                new GamePlayerForfeitEvent(gameId, gamePlayerDto)
        );

        // UPDATE STATS - LOSS INCURRED
        // Call submitGameResult

        if (game.getGameStatus() == GameStatus.ABORTED) {
            return null;
        }

        return gameDto;
    }

    @Transactional
    void abortGame(GameEntity game) {
        game.abortGame();
        // Update membership and in memory caches
        applicationEventPublisher.publishEvent(
                new GameClosedEvent(game.getId(), game.getLobbyEntity().getId())
        );
    }

    @Transactional
    public void handlePlayerFinish(GamePlayerEntity gamePlayer) {
        if (gamePlayer.isFinishedGame()) {
            return;
        }
        gamePlayer.markGameFinished();
        Integer leaderboardScore = gamePlayer.getLeaderboardScore();
        submitLeaderboardScore(leaderboardScore);
        gamePlayer.setLeaderboardScore(leaderboardScore);
    }

    // Mark all players as having finished the game, triggering leaderboard stats calculation
    @Transactional
    public GameDto finishGame(Long gameId) {
        GameEntity game = getGameById(gameId);
        game.finishGame();
        Set<GamePlayerEntity> activePlayers = game.getRemainingActivePlayers();
        activePlayers.forEach(this::handlePlayerFinish);
        gameRepository.save(game);
        return gameEntityDtoMapper.mapToDto(game);
    }

    // Once game is closed, it cannot be navigated back to
    @Transactional
    public GameDto closeGame(Long gameId) {
        GameEntity game = getGameById(gameId);
        game.closeGame();

        // Handle determining and persisting player results (win, lose, draw)

        // IMPLEMENT LOGIC

        // Update membership and in memory caches
        applicationEventPublisher.publishEvent(
                new GameClosedEvent(gameId, game.getLobbyEntity().getId())
        );

        // ADD WS EVENTS

        // CHANGE
        return new GameDto();
    }

    @Transactional
    void submitLeaderboardScore(Integer leaderboardScore) {
        // IMPLEMENT - persists score in leaderboard entity
    }

    // Update leaderboard with player's game result e.g. win/loss
    @Transactional
    void submitGameResults(GameResult gameResult, Integer leaderboardScore) {

    }
}
