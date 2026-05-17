package com.github.ryand6.sudokuweb.services.game;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventRequest;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventSequenceEntity;
import com.github.ryand6.sudokuweb.domain.game.event.GameEventSequenceRepository;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerFactory;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerRepository;
import com.github.ryand6.sudokuweb.domain.game.player.LeaderboardScoreCalculation;
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
import com.github.ryand6.sudokuweb.events.types.game.*;
import com.github.ryand6.sudokuweb.events.types.lobby.EndLobbyPlayerInGameStatusEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyCountdownResetEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.ws.LobbyUpdatePostGameCreationWsEvent;
import com.github.ryand6.sudokuweb.exceptions.game.GameCreationInterruptedException;
import com.github.ryand6.sudokuweb.exceptions.game.GameNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.player.GamePlayerNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.player.PlayerNotFinishedGameException;
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
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final SudokuPuzzleService sudokuPuzzleService;
    private final GameEntityDtoMapper gameEntityDtoMapper;
    private final GamePlayerEntityDtoMapper gamePlayerEntityDtoMapper;
    private final GamePlayerStateRepository gamePlayerStateRepository;
    private final GamePlayerRepository gamePlayerRepository;
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
                       GamePlayerRepository gamePlayerRepository,
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
        this.gamePlayerRepository = gamePlayerRepository;
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
            lobbyPlayerEntity.setLobbyStatus(LobbyStatus.INGAME);
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

        if (game.validateGameEndedPrematurely()) {
            GamePlayerEntity lastRemainingPlayer = game.findLastRemainingPlayer();
            if (lastRemainingPlayer != null) {
                handlePlayerFinish(gameId, lastRemainingPlayer.getUserEntity().getId());

                applicationEventPublisher.publishEvent(
                        new GameEndedPrematurelyEvent(gameId)
                );
            }
        }

        // IMPLEMENT - handle leaderboard entity game result update - loss

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
        applicationEventPublisher.publishEvent(
                new GameStatusUpdateEvent(game.getId(), GameStatus.ABORTED)
        );
    }

    @Transactional
    public LeaderboardScoreCalculation getPlayerLeaderboardResult(Long gameId, Long userId) {
        GameEntity gameEntity = getGameById(gameId);
        GamePlayerEntity gamePlayer = findGamePlayer(gameEntity, userId);
        if (!gamePlayer.isFinishedGame()) {
            throw new PlayerNotFinishedGameException("Unable to retrieve leaderboard score. Game not finished.");
        }
        return gamePlayer.calculateLeaderboardScore();
    }

    @Transactional
    public void handlePlayerFinish(Long gameId, Long userId) {
        GamePlayerEntity gamePlayer = gamePlayerRepository.findByCompositeId(gameId, userId).orElseThrow(() ->
                new GamePlayerNotFoundException("Game Player with Game ID " + gameId + " and User ID " + userId + " does not exist"));
        if (gamePlayer.isFinishedGame()) {
            return;
        }
        gamePlayer.markGameFinished();

        GamePlayerDto gamePlayerDto = gamePlayerEntityDtoMapper.mapToDto(gamePlayer);

        applicationEventPublisher.publishEvent(
                new PlayerFinishedGameEvent(gameId, gamePlayerDto)
        );

        LeaderboardScoreCalculation leaderboardScoreCalculation = gamePlayer.calculateLeaderboardScore();
        Integer leaderboardScore = leaderboardScoreCalculation.getFinalScore();
        gamePlayer.setLeaderboardScore(leaderboardScore);
        submitLeaderboardScore(leaderboardScore);

        applicationEventPublisher.publishEvent(
                new PlayerLeaderboardScoreEvent(gameId, gamePlayer.getUserEntity().getId(), leaderboardScoreCalculation)
        );

        // IMPLEMENT - handle leaderboard entity score update

        if (gamePlayer.getGameEntity().isGameFinished()) {
            finishGame(gamePlayer.getGameEntity());
        }
    }

    // Mark game as finished and send event to determine game result of each player
    @Transactional
    void finishGame(GameEntity game) {
        game.finishGame();
        handleGameResults(game.getId());
        gameRepository.save(game);

        applicationEventPublisher.publishEvent(
                new GameStatusUpdateEvent(game.getId(), GameStatus.FINISHED)
        );

    }

    @Transactional
    public void handleGameResults(Long gameId) {
        GameEntity game = getGameById(gameId);
        Set<GamePlayerEntity> gamePlayers = game.getGamePlayerEntities();
        if (game.getGameSettingsEntity().getGameMode() == GameMode.TIMEATTACK) {
            boolean gameWon = game.determineTimeAttackVictory();
            GameResult gameResult = gameWon ? GameResult.WIN : GameResult.LOSS;
            for (GamePlayerEntity gamePlayer : gamePlayers) {
                gamePlayer.setGameResult(gameResult);
            }
        } else {
            Set<GamePlayerEntity> winners = game.determineGameWinners();
            for (GamePlayerEntity gamePlayer : gamePlayers) {
                GameResult gameResult = !winners.contains(gamePlayer) ? GameResult.LOSS :
                        winners.size() == 1 ? GameResult.WIN : GameResult.DRAW;
                gamePlayer.setGameResult(gameResult);
            }
        }
        Map<Long, GameResult> gameResults = gamePlayers.stream()
                .collect(Collectors.toMap(
                        gamePlayer -> gamePlayer.getUserEntity().getId(),
                        GamePlayerEntity::getGameResult));

        applicationEventPublisher.publishEvent(
                new GameResultsDeterminedEvent(gameId, gameResults)
        );

        // IMPLEMENT - handle leaderboard entity game result updates
    }


    // Once game is closed, it cannot be navigated back to
    @Transactional
    public void closeGame(Long gameId) {
        GameEntity game = getGameById(gameId);
        game.closeGame();

        // Update membership and in memory caches
        applicationEventPublisher.publishEvent(
                new GameClosedEvent(gameId, game.getLobbyEntity().getId())
        );

        applicationEventPublisher.publishEvent(
                new GameStatusUpdateEvent(gameId, GameStatus.CLOSED)
        );

        applicationEventPublisher.publishEvent(
                new EndLobbyPlayerInGameStatusEvent(game.getLobbyEntity().getId())
        );
    }

    @Transactional
    void submitLeaderboardScore(Integer leaderboardScore) {
        // IMPLEMENT - persists score in leaderboard entity
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    void handlePlayerFinishEvent(HandlePlayerFinishEvent event) {
        handlePlayerFinish(event.getGameId(), event.getUserId());
    }

}
