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
import com.github.ryand6.sudokuweb.events.types.game.GameClosedEvent;
import com.github.ryand6.sudokuweb.events.types.game.GamePlayerLeftEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.LobbyCountdownResetEvent;
import com.github.ryand6.sudokuweb.events.types.lobby.ws.LobbyUpdatePostGameCreationWsEvent;
import com.github.ryand6.sudokuweb.exceptions.game.GameCreationInterruptedException;
import com.github.ryand6.sudokuweb.exceptions.game.GameNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.game.state.GamePlayerStateNotFoundException;
import com.github.ryand6.sudokuweb.exceptions.lobby.LobbyNotFoundException;
import com.github.ryand6.sudokuweb.mappers.Impl.game.GameEntityDtoMapper;
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
    private final GamePlayerStateRepository gamePlayerStateRepository;
    private final PrivateGamePlayerStateEntityDtoMapper privateGamePlayerStateEntityDtoMapper;
    private final LobbyRepository lobbyRepository;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public GameService(GameRepository gameRepository,
                       SudokuPuzzleService sudokuPuzzleService,
                       GameEntityDtoMapper gameEntityDtoMapper,
                       GamePlayerStateRepository gamePlayerStateRepository,
                       PrivateGamePlayerStateEntityDtoMapper privateGamePlayerStateEntityDtoMapper,
                       LobbyRepository lobbyRepository,
                       LobbyEntityDtoMapper lobbyEntityDtoMapper,
                       ApplicationEventPublisher applicationEventPublisher) {
        this.gameRepository = gameRepository;
        this.sudokuPuzzleService = sudokuPuzzleService;
        this.gameEntityDtoMapper = gameEntityDtoMapper;
        this.gamePlayerStateRepository = gamePlayerStateRepository;
        this.privateGamePlayerStateEntityDtoMapper = privateGamePlayerStateEntityDtoMapper;
        this.lobbyRepository = lobbyRepository;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.applicationEventPublisher = applicationEventPublisher;
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
            GamePlayerEntity gamePlayer = GamePlayerFactory.createGamePlayer(newGame, lobbyPlayerEntity.getUser(), playerColour, newGame.isGameStateShared(), newGame.getSudokuPuzzleEntity().getInitialBoardState());
            gamePlayers.add(gamePlayer);
            i++;
        }

        return gameEntityDtoMapper.mapToDto(newGame);
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

    @Transactional
    public GameDto removeGamePlayer(Long gameId, Long userId) {
        GameEntity game = getGameById(gameId);

        // IMPLEMENT LOGIC

        // Send event to remove player from lobby and update membership/in memory caches
        applicationEventPublisher.publishEvent(
                new GamePlayerLeftEvent(game.getId(), game.getLobbyEntity().getId(), userId)
        );

        // ADD WS EVENTS

        // CHANGE
        return new GameDto();
    }

    @Transactional
    public GameDto endGame(Long gameId) {
        // IMPLEMENT LOGIC

        // Update membership and in memory caches
        applicationEventPublisher.publishEvent(
                new GameClosedEvent(gameId)
        );

        // ADD WS EVENTS

        // CHANGE
        return new GameDto();
    }

}
