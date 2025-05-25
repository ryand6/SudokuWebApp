package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.dto.GameStateDto;
import com.github.ryand6.sudokuweb.mappers.Impl.GameStateEntityDtoMapper;
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
Integration tests for LobbyStateEntityDtoMapper
*/
public class GameStateEntityDtoMapperIntegrationTests {

    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final GameStateEntityDtoMapper gameStateEntityDtoMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GameStateEntityDtoMapperIntegrationTests(
            LobbyRepository lobbyRepository,
            UserRepository userRepository,
            SudokuPuzzleRepository sudokuPuzzleRepository,
            GameStateEntityDtoMapper gameStateEntityDtoMapper,
            JdbcTemplate jdbcTemplate) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.gameStateEntityDtoMapper = gameStateEntityDtoMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private ScoreEntity savedScore;
    private UserEntity savedUser;
    private SudokuPuzzleEntity savedPuzzle;
    private LobbyEntity savedLobby;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
        // Setup test score data - don't save as the User object will save it via cascade.ALL
        savedScore = TestDataUtil.createTestScoreA();
        // Setup test user data in the test DB
        savedUser = userRepository.save(TestDataUtil.createTestUserA(savedScore));
        // Setup test puzzle data in the test DB
        savedPuzzle = sudokuPuzzleRepository.save(TestDataUtil.createTestSudokuPuzzleA());
        //Setup test lobby data in the test DB
        savedLobby = lobbyRepository.save(TestDataUtil.createTestLobbyA(savedUser));
    }

    @Test
    void mapToDto_shouldReturnValidLobbyStateDto() {
        GameStateEntity gameStateEntity = TestDataUtil.createTestLobbyStateA(savedLobby, savedUser, savedPuzzle);
        gameStateEntity.setId(6L);

        GameStateDto gameStateDto = gameStateEntityDtoMapper.mapToDto(gameStateEntity);

        assertThat(gameStateDto).isNotNull();
        assertThat(gameStateDto.getId()).isEqualTo(6L);
        assertThat(gameStateDto.getLobbyId()).isEqualTo(savedLobby.getId());
        assertThat(gameStateDto.getUserId()).isEqualTo(savedUser.getId());
        assertThat(gameStateDto.getPuzzleId()).isEqualTo(savedPuzzle.getId());
        assertThat(gameStateDto.getUsername()).isEqualTo(savedUser.getUsername());
        assertThat(gameStateDto.getScore()).isEqualTo(0);
        assertThat(gameStateDto.getCurrentBoardState()).isEqualTo("092306001007008003043207080035680000080000020000035670070801950200500800500409130");
        assertThat(gameStateDto.getLastActive()).isEqualTo(gameStateEntity.getLastActive());
    }

    @Test
    void mapFromDto_shouldReturnValidLobbyStateEntity() {
        GameStateDto gameStateDto = GameStateDto.builder()
                .id(2L)
                .lobbyId(savedLobby.getId())
                .userId(savedUser.getId())
                .puzzleId(savedPuzzle.getId())
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        GameStateEntity gameStateEntity = gameStateEntityDtoMapper.mapFromDto(gameStateDto);

        assertThat(gameStateEntity).isNotNull();
        assertThat(gameStateEntity.getId()).isEqualTo(2L);
        assertThat(gameStateEntity.getLobbyEntity().getId()).isNotNull();
        assertThat(gameStateEntity.getUserEntity().getId()).isNotNull();
        assertThat(gameStateEntity.getSudokuPuzzleEntity().getId()).isNotNull();
        assertThat(gameStateEntity.getUserEntity().getUsername()).isEqualTo("Henry");
        assertThat(gameStateEntity.getUserEntity().getPasswordHash()).isEqualTo("a4ceE42GHa");
        assertThat(gameStateEntity.getScore()).isEqualTo(55);
        assertThat(gameStateEntity.getCurrentBoardState()).isEqualTo("973004000000006900000329000007008010680932075090400600000295000002100000000800020");
    }

    @Test
    void mapFromDto_shouldThrowException_whenUserNotFound() {
        GameStateDto gameStateDto = GameStateDto.builder()
                .id(2L)
                .lobbyId(savedLobby.getId())
                .userId(999999L)
                .puzzleId(savedPuzzle.getId())
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> gameStateEntityDtoMapper.mapFromDto(gameStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void mapFromDto_shouldThrowException_whenLobbyNotFound() {
        GameStateDto gameStateDto = GameStateDto.builder()
                .id(2L)
                .lobbyId(99999L)
                .userId(savedUser.getId())
                .puzzleId(savedPuzzle.getId())
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> gameStateEntityDtoMapper.mapFromDto(gameStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lobby not found");
    }

    @Test
    void mapFromDto_shouldThrowException_whenSudokuPuzzleNotFound() {
        GameStateDto gameStateDto = GameStateDto.builder()
                .id(2L)
                .lobbyId(savedLobby.getId())
                .userId(savedUser.getId())
                .puzzleId(99999L)
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> gameStateEntityDtoMapper.mapFromDto(gameStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Puzzle not found");
    }

}
