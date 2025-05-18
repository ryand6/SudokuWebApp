package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.*;
import com.github.ryand6.sudokuweb.dto.LobbyStateDto;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyStateEntityDtoMapper;
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
public class LobbyStateEntityDtoMapperIntegrationTests {

    private final LobbyRepository lobbyRepository;
    private final UserRepository userRepository;
    private final SudokuPuzzleRepository sudokuPuzzleRepository;
    private final LobbyStateEntityDtoMapper lobbyStateEntityDtoMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LobbyStateEntityDtoMapperIntegrationTests(
            LobbyRepository lobbyRepository,
            UserRepository userRepository,
            SudokuPuzzleRepository sudokuPuzzleRepository,
            LobbyStateEntityDtoMapper lobbyStateEntityDtoMapper,
            JdbcTemplate jdbcTemplate) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
        this.sudokuPuzzleRepository = sudokuPuzzleRepository;
        this.lobbyStateEntityDtoMapper = lobbyStateEntityDtoMapper;
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
        LobbyStateEntity lobbyStateEntity = TestDataUtil.createTestLobbyStateA(savedLobby, savedUser, savedPuzzle);
        lobbyStateEntity.setId(6L);

        LobbyStateDto lobbyStateDto = lobbyStateEntityDtoMapper.mapToDto(lobbyStateEntity);

        assertThat(lobbyStateDto).isNotNull();
        assertThat(lobbyStateDto.getId()).isEqualTo(6L);
        assertThat(lobbyStateDto.getLobbyId()).isEqualTo(savedLobby.getId());
        assertThat(lobbyStateDto.getUserId()).isEqualTo(savedUser.getId());
        assertThat(lobbyStateDto.getPuzzleId()).isEqualTo(savedPuzzle.getId());
        assertThat(lobbyStateDto.getUsername()).isEqualTo(savedUser.getUsername());
        assertThat(lobbyStateDto.getScore()).isEqualTo(0);
        assertThat(lobbyStateDto.getCurrentBoardState()).isEqualTo("092306001007008003043207080035680000080000020000035670070801950200500800500409130");
        assertThat(lobbyStateDto.getLastActive()).isEqualTo(lobbyStateEntity.getLastActive());
    }

    @Test
    void mapFromDto_shouldReturnValidLobbyStateEntity() {
        LobbyStateDto lobbyStateDto = LobbyStateDto.builder()
                .id(2L)
                .lobbyId(savedLobby.getId())
                .userId(savedUser.getId())
                .puzzleId(savedPuzzle.getId())
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        LobbyStateEntity lobbyStateEntity = lobbyStateEntityDtoMapper.mapFromDto(lobbyStateDto);

        assertThat(lobbyStateEntity).isNotNull();
        assertThat(lobbyStateEntity.getId()).isEqualTo(2L);
        assertThat(lobbyStateEntity.getLobbyEntity().getId()).isNotNull();
        assertThat(lobbyStateEntity.getUserEntity().getId()).isNotNull();
        assertThat(lobbyStateEntity.getSudokuPuzzleEntity().getId()).isNotNull();
        assertThat(lobbyStateEntity.getUserEntity().getUsername()).isEqualTo("Henry");
        assertThat(lobbyStateEntity.getUserEntity().getPasswordHash()).isEqualTo("a4ceE42GHa");
        assertThat(lobbyStateEntity.getScore()).isEqualTo(55);
        assertThat(lobbyStateEntity.getCurrentBoardState()).isEqualTo("973004000000006900000329000007008010680932075090400600000295000002100000000800020");
    }

    @Test
    void mapFromDto_shouldThrowException_whenUserNotFound() {
        LobbyStateDto lobbyStateDto = LobbyStateDto.builder()
                .id(2L)
                .lobbyId(savedLobby.getId())
                .userId(999999L)
                .puzzleId(savedPuzzle.getId())
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> lobbyStateEntityDtoMapper.mapFromDto(lobbyStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void mapFromDto_shouldThrowException_whenLobbyNotFound() {
        LobbyStateDto lobbyStateDto = LobbyStateDto.builder()
                .id(2L)
                .lobbyId(99999L)
                .userId(savedUser.getId())
                .puzzleId(savedPuzzle.getId())
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> lobbyStateEntityDtoMapper.mapFromDto(lobbyStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Lobby not found");
    }

    @Test
    void mapFromDto_shouldThrowException_whenSudokuPuzzleNotFound() {
        LobbyStateDto lobbyStateDto = LobbyStateDto.builder()
                .id(2L)
                .lobbyId(savedLobby.getId())
                .userId(savedUser.getId())
                .puzzleId(99999L)
                .username(savedUser.getUsername())
                .score(55)
                .currentBoardState("973004000000006900000329000007008010680932075090400600000295000002100000000800020")
                .build();

        assertThatThrownBy(() -> lobbyStateEntityDtoMapper.mapFromDto(lobbyStateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Puzzle not found");
    }

}
