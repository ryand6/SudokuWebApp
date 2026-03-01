package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.domain.score.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import com.github.ryand6.sudokuweb.mappers.Impl.GameEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.GameStateEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.SudokuPuzzleEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.GameRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
Integration tests for GameEntityDtoMapper
*/
public class GameEntityDtoMapperIntegrationTests extends AbstractIntegrationTest {

    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final SudokuPuzzleEntityDtoMapper sudokuPuzzleEntityDtoMapper;
    private final GameStateEntityDtoMapper gameStateEntityDtoMapper;
    private final GameEntityDtoMapper gameEntityDtoMapper;
    private final GameRepository gameRepository;

    @Autowired
    public GameEntityDtoMapperIntegrationTests(
            LobbyRepository lobbyRepository,
            UserRepository userRepository,
            SudokuPuzzleRepository sudokuPuzzleRepository,
            LobbyEntityDtoMapper lobbyEntityDtoMapper,
            SudokuPuzzleEntityDtoMapper sudokuPuzzleEntityDtoMapper,
            GameStateEntityDtoMapper gameStateEntityDtoMapper,
            GameEntityDtoMapper gameEntityDtoMapper,
            GameRepository gameRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.sudokuPuzzleEntityDtoMapper = sudokuPuzzleEntityDtoMapper;
        this.gameStateEntityDtoMapper = gameStateEntityDtoMapper;
        this.gameEntityDtoMapper = gameEntityDtoMapper;
        this.gameRepository = gameRepository;
    }

    private ScoreEntity savedScore;
    private UserEntity savedUser;
    private SudokuPuzzleEntity savedPuzzle;
    private LobbyEntity savedLobby;

//    @BeforeEach
//    public void setUp() {
//        // Setup test score data - don't save as the User object will save it via cascade.ALL
//        savedScore = TestDataUtil.createTestScoreA();
//        // Setup test user data in the test DB
//        savedUser = userRepository.save(TestDataUtil.createTestUserA(savedScore));
//        // Setup test puzzle data in the test DB
//        savedPuzzle = sudokuPuzzleRepository.save(TestDataUtil.createTestSudokuPuzzleA());
//        //Setup test lobby data in the test DB
//        savedLobby = lobbyRepository.save(TestDataUtil.createTestLobbyA(savedUser));
//        LobbyPlayerEntity lobbyPlayerEntity = TestDataUtil.createTestLobbyPlayer(savedLobby, savedUser);
//        savedLobby.setLobbyPlayers(Set.of(lobbyPlayerEntity));
//    }
//
//    @Test
//    void mapToDto_shouldReturnValidGameDto() {
//        GameEntity gameEntity = TestDataUtil.createTestGame(savedLobby, savedPuzzle);
//        gameEntity.setId(6L);
//
//        // Get list of player colour enums
//        PlayerColour[] playerColours = PlayerColour.values();
//        int i = 0;
//
//        Set<LobbyPlayerEntity> activeLobbyPlayers = savedLobby.getLobbyPlayers();
//
//        // Create GameState objects for each active user in the game
//        Set<GameStateEntity> gameStateEntities = new HashSet<>();
//        for (LobbyPlayerEntity lobbyPlayerEntity : activeLobbyPlayers) {
//            GameStateEntity state = new GameStateEntity();
//            state.setUserEntity(lobbyPlayerEntity.getUser());
//            state.setGameEntity(gameEntity);
//            // Board state starts with the initial sudokuPuzzleEntity
//            state.setCurrentBoardState(savedPuzzle.getInitialBoardState());
//            // Initial score for each user is 0
//            state.setScore(0);
//            // Set the player colour and increment the counter so the next player colour is unique
//            state.setPlayerColour(playerColours[i]);
//            i++;
//            gameStateEntities.add(state);
//        }
//        gameEntity.setGameStateEntities(gameStateEntities);
//        gameRepository.save(gameEntity);
//
//        GameDto gameDto = gameEntityDtoMapper.mapToDto(gameEntity);
//
//        Set<Long> gameStateDtoIds = new HashSet<>();
//        for (GameStateEntity gameStateEntity : gameStateEntities) {
//            gameStateDtoIds.add(gameStateEntityDtoMapper.mapToDto(gameStateEntity).getGameId());
//        }
//
//        assertThat(gameDto).isNotNull();
//        assertThat(gameDto.getId()).isEqualTo(6L);
//        assertThat(gameDto.getLobby().getId()).isEqualTo(savedLobby.getId());
//        assertThat(gameDto.getSudokuPuzzle().getId()).isEqualTo(savedPuzzle.getId());
//        assertThat(gameDto.getGameStates()).hasSize(1);
//        assertThat(gameDto.getGameStates().iterator().next().getGameId()).isIn(gameStateDtoIds);
//    }

//    @Test
//    void mapFromDto_shouldReturnValidGameEntity() {
//        GameEntity gameEntity = TestDataUtil.createTestGame(savedLobby, savedPuzzle);
//
//        // Get list of player colour enums
//        PlayerColour[] playerColours = PlayerColour.values();
//        int i = 0;
//
//        Set<LobbyPlayerEntity> activeLobbyPlayers = savedLobby.getLobbyPlayers();
//
//        // Create GameState objects for each active user in the game
//        Set<GameStateEntity> gameStateEntities = new HashSet<>();
//        for (LobbyPlayerEntity lobbyPlayerEntity : activeLobbyPlayers) {
//            GameStateEntity state = new GameStateEntity();
//            state.setUserEntity(lobbyPlayerEntity.getUser());
//            state.setGameEntity(gameEntity);
//            // Board state starts with the initial sudokuPuzzleEntity
//            state.setCurrentBoardState(savedPuzzle.getInitialBoardState());
//            // Initial score for each user is 0
//            state.setScore(0);
//            // Set the player colour and increment the counter so the next player colour is unique
//            state.setPlayerColour(playerColours[i]);
//            i++;
//            gameStateEntities.add(state);
//        }
//        gameEntity.setGameStateEntities(gameStateEntities);
//        gameRepository.save(gameEntity);
//
//        GameDto gameDto = gameEntityDtoMapper.mapToDto(gameEntity);
//
//        GameEntity testGameEntity = gameEntityDtoMapper.mapFromDto(gameDto);
//
//        assertThat(testGameEntity).isNotNull();
//        assertThat(testGameEntity.getLobbyEntity()).isEqualTo(savedLobby);
//        assertThat(testGameEntity.getSudokuPuzzleEntity()).isEqualTo(savedPuzzle);
//        assertThat(testGameEntity.getGameStateEntities()).hasSize(1);
//        assertThat(testGameEntity.getGameStateEntities()).isEqualTo(gameStateEntities);
//    }
//
//    @Test
//    void mapFromDto_shouldThrowException_whenLobbyNotFound() {
//        ScoreEntity testScore = TestDataUtil.createTestScoreB();
//        UserEntity testUser = userRepository.save(TestDataUtil.createTestUserB(testScore));
//
//        LobbyEntity nonPersistedLobby = TestDataUtil.createTestLobbyA(testUser);
//        nonPersistedLobby.setId(99999L);
//
//        GameDto gameDto = GameDto.builder()
//                .id(2L)
//                .lobby(lobbyEntityDtoMapper.mapToDto(nonPersistedLobby))
//                .sudokuPuzzle(sudokuPuzzleEntityDtoMapper.mapToDto(savedPuzzle))
//                .build();
//
//        assertThatThrownBy(() -> gameEntityDtoMapper.mapFromDto(gameDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Lobby not found");
//    }
//
//    @Test
//    void mapFromDto_shouldThrowException_whenPuzzleNotFound() {
//        SudokuPuzzleEntity testPuzzle = TestDataUtil.createTestSudokuPuzzleB();
//        testPuzzle.setId(99999L);
//
//        GameDto gameDto = GameDto.builder()
//                .id(2L)
//                .lobby(lobbyEntityDtoMapper.mapToDto(savedLobby))
//                .sudokuPuzzle(sudokuPuzzleEntityDtoMapper.mapToDto(testPuzzle))
//                .build();
//
//        assertThatThrownBy(() -> gameEntityDtoMapper.mapFromDto(gameDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Puzzle not found");
//    }
//
//    @Test
//    void mapFromDto_shouldThrowException_whenGameNotFound() {
//        GameEntity gameEntity = TestDataUtil.createTestGame(savedLobby, savedPuzzle);
//
//        // Get list of player colour enums
//        PlayerColour[] playerColours = PlayerColour.values();
//        int i = 0;
//
//        Set<LobbyPlayerEntity> activeLobbyPlayers = savedLobby.getLobbyPlayers();
//
//        // Create GameState objects for each active user in the game
//        Set<GameStateEntity> gameStateEntities = new HashSet<>();
//        for (LobbyPlayerEntity lobbyPlayerEntity : activeLobbyPlayers) {
//            GameStateEntity state = new GameStateEntity();
//            state.setUserEntity(lobbyPlayerEntity.getUser());
//            state.setGameEntity(gameEntity);
//            // Board state starts with the initial sudokuPuzzleEntity
//            state.setCurrentBoardState(savedPuzzle.getInitialBoardState());
//            // Initial score for each user is 0
//            state.setScore(0);
//            // Set the player colour and increment the counter so the next player colour is unique
//            state.setPlayerColour(playerColours[i]);
//            i++;
//            gameStateEntities.add(state);
//        }
//        gameEntity.setGameStateEntities(gameStateEntities);
//        gameRepository.save(gameEntity);
//
//        GameDto gameDto = gameEntityDtoMapper.mapToDto(gameEntity);
//        for (GameStateDto gameStateDto : gameDto.getGameStates()) {
//            gameStateDto.setId(99999L);
//        }
//
//        assertThatThrownBy(() -> gameEntityDtoMapper.mapFromDto(gameDto))
//                .isInstanceOf(EntityNotFoundException.class)
//                .hasMessageContaining("Game state not found");
//    }

}
