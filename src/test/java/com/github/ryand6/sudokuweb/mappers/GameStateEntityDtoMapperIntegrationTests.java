package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.dto.GameStateDto;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import com.github.ryand6.sudokuweb.mappers.Impl.GameStateEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.GameRepository;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import com.github.ryand6.sudokuweb.repositories.SudokuPuzzleRepository;
import com.github.ryand6.sudokuweb.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
/*
Integration tests for GameStateEntityDtoMapper
*/
public class GameStateEntityDtoMapperIntegrationTests {

    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final GameStateEntityDtoMapper gameStateEntityDtoMapper;
    private final JdbcTemplate jdbcTemplate;
    private final UserEntityDtoMapper userEntityDtoMapper;
    private final GameRepository gameRepository;

    @Autowired
    public GameStateEntityDtoMapperIntegrationTests(
            LobbyRepository lobbyRepository,
            UserRepository userRepository,
            SudokuPuzzleRepository sudokuPuzzleRepository,
            GameStateEntityDtoMapper gameStateEntityDtoMapper,
            JdbcTemplate jdbcTemplate,
            UserEntityDtoMapper userEntityDtoMapper,
            GameRepository gameRepository) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.gameStateEntityDtoMapper = gameStateEntityDtoMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.userEntityDtoMapper = userEntityDtoMapper;
        this.gameRepository = gameRepository;
    }

    private ScoreEntity savedScore;
    private UserEntity savedUser;
    private SudokuPuzzleEntity savedPuzzle;
    private LobbyEntity savedLobby;
    private GameEntity gameEntity;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM game_state");
        jdbcTemplate.execute("DELETE FROM games");
        jdbcTemplate.execute("DELETE FROM lobby_players");
        jdbcTemplate.execute("DELETE FROM lobbies");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        // Setup test score data - don't save as the User object will save it via cascade.ALL
        savedScore = TestDataUtil.createTestScoreA();
        // Setup test user data in the test DB
        savedUser = userRepository.save(TestDataUtil.createTestUserA(savedScore));
        // Setup test puzzle data in the test DB
        savedPuzzle = sudokuPuzzleRepository.save(TestDataUtil.createTestSudokuPuzzleA());
        //Setup test lobby data in the test DB
        savedLobby = lobbyRepository.save(TestDataUtil.createTestLobbyA(savedUser));
        // Prepare a game entity so that it can be linked to the game state entities
        gameEntity = TestDataUtil.createTestGame(savedLobby, savedPuzzle);
    }

    @Test
    void mapToDto_shouldReturnValidGameStateDto() {
        GameStateEntity gameStateEntity = TestDataUtil.createTestGameStateA(gameEntity, savedUser);
        gameStateEntity.setId(6L);

        GameStateDto gameStateDto = gameStateEntityDtoMapper.mapToDto(gameStateEntity);

        assertThat(gameStateDto).isNotNull();
        assertThat(gameStateDto.getId()).isEqualTo(6L);
        assertThat(gameStateDto.getGameId()).isEqualTo(gameEntity.getId());
        assertThat(gameStateDto.getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(gameStateDto.getScore()).isEqualTo(0);
        assertThat(gameStateDto.getPlayerColour()).isEqualTo(PlayerColour.BLUE);
        assertThat(gameStateDto.getCurrentBoardState()).isEqualTo("092306001007008003043207080035680000080000020000035670070801950200500800500409130");
    }

    @Test
    void mapFromDto_shouldReturnValidGameStateEntity() {
        gameRepository.save(gameEntity);

        GameStateDto gameStateDto = GameStateDto.builder()
                .id(2L)
                .gameId(gameEntity.getId())
                .user(userEntityDtoMapper.mapToDto(savedUser))
                .score(55)
                .playerColour(PlayerColour.ORANGE)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        GameStateEntity gameStateEntity = gameStateEntityDtoMapper.mapFromDto(gameStateDto);

        assertThat(gameStateEntity).isNotNull();
        assertThat(gameStateEntity.getId()).isEqualTo(2L);
        assertThat(gameStateEntity.getGameEntity().getId()).isNotNull();
        assertThat(gameStateEntity.getUserEntity().getId()).isNotNull();
        assertThat(gameStateEntity.getUserEntity().getUsername()).isEqualTo("Henry");
        assertThat(gameStateEntity.getScore()).isEqualTo(55);
        assertThat(gameStateEntity.getPlayerColour()).isEqualTo(PlayerColour.ORANGE);
        assertThat(gameStateEntity.getCurrentBoardState()).isEqualTo("973004000000006900000329000007008010680932075090400600000295000002100000000800020");
    }

    @Test
    void mapFromDto_shouldThrowException_whenUserNotFound() {
        gameRepository.save(gameEntity);
        UserEntity nonPersistedUser = TestDataUtil.createTestUserB(savedScore);
        nonPersistedUser.setId(999999L);

        GameStateDto gameStateDto = GameStateDto.builder()
                .id(2L)
                .gameId(gameEntity.getId())
                .user(userEntityDtoMapper.mapToDto(nonPersistedUser))
                .score(55)
                .playerColour(PlayerColour.GREEN)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> gameStateEntityDtoMapper.mapFromDto(gameStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    // Game not persisted
    @Test
    void mapFromDto_shouldThrowException_whenGameNotFound() {
        GameStateDto gameStateDto = GameStateDto.builder()
                .id(2L)
                .gameId(9999999L)
                .user(userEntityDtoMapper.mapToDto(savedUser))
                .score(55)
                .playerColour(PlayerColour.PURPLE)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> gameStateEntityDtoMapper.mapFromDto(gameStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Game not found");
    }

}
