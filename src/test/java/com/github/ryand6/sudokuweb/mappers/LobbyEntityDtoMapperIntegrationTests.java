package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.LobbyDto;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.mappers.Impl.LobbyEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.ScoreEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
/*
Integration tests for LobbyEntityDtoMapper
*/
public class LobbyEntityDtoMapperIntegrationTests {

    private final UserRepository userRepository;
    private final LobbyRepository lobbyRepository;
    private final LobbyEntityDtoMapper lobbyEntityDtoMapper;
    private final ScoreEntityDtoMapper scoreEntityDtoMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LobbyEntityDtoMapperIntegrationTests(
            UserRepository userRepository,
            LobbyRepository lobbyRepository,
            LobbyEntityDtoMapper lobbyEntityDtoMapper,
            ScoreEntityDtoMapper scoreEntityDtoMapper,
            JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
        this.lobbyEntityDtoMapper = lobbyEntityDtoMapper;
        this.scoreEntityDtoMapper = scoreEntityDtoMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private ScoreEntity savedScore;
    private UserEntity savedUser;
    private LobbyEntity savedLobby;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM game_state");
        jdbcTemplate.execute("DELETE FROM games");
        jdbcTemplate.execute("DELETE FROM lobbies");
        jdbcTemplate.execute("DELETE FROM lobbyPlayers");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        // Setup test score data - don't save as the User object will save it via cascade.ALL
        savedScore = TestDataUtil.createTestScoreA();
        // Setup test user data in the test DB
        savedUser = userRepository.save(TestDataUtil.createTestUserA(savedScore));
    }

    @Test
    void mapToDto_shouldReturnValidLobbyDto() {
        savedLobby = lobbyRepository.save(TestDataUtil.createTestLobbyA(savedUser));

        LobbyDto dto = lobbyEntityDtoMapper.mapToDto(savedLobby);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(savedLobby.getId());
        assertThat(dto.getLobbyName()).isEqualTo(savedLobby.getLobbyName());
        assertThat(dto.getIsActive()).isEqualTo(savedLobby.getIsActive());
        assertThat(dto.getIsPublic()).isEqualTo(savedLobby.getIsPublic());
        assertThat(dto.getInGame()).isEqualTo(savedLobby.getInGame());
        assertThat(dto.getDifficulty()).isEqualTo(savedLobby.getDifficulty());
        assertThat(dto.getHostId()).isEqualTo(savedLobby.getHost().getId());

        assertThat(dto.getLobbyPlayers()).hasSize(1);
        UserDto userDto = dto.getLobbyPlayers().iterator().next();
        assertThat(userDto.getId()).isEqualTo(savedUser.getId());
        assertThat(userDto.getUsername()).isEqualTo(savedUser.getUsername());
    }

    @Test
    void mapFromDto_shouldReturnValidLobbyEntity() {
        // Create a LobbyDto from saved user
        UserDto userDto = UserDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .isOnline(true)
                .score(scoreEntityDtoMapper.mapToDto(savedScore))
                .build();

        LobbyDto dto = LobbyDto.builder()
                .id(5L)
                .lobbyName("Test Lobby")
                .isActive(true)
                .isPublic(false)
                .inGame(false)
                .lobbyPlayers(Set.of(userDto))
                .hostId(userDto.getId())
                .build();

        LobbyEntity entity = lobbyEntityDtoMapper.mapFromDto(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(5L);
        assertThat(entity.getLobbyName()).isEqualTo("Test Lobby");
        assertThat(entity.getIsActive()).isTrue();
        assertThat(entity.getIsPublic()).isFalse();
        assertThat(entity.getInGame()).isFalse();
        assertThat(entity.getUserEntities()).hasSize(1);
        assertThat(entity.getHost().getId()).isEqualTo(savedUser.getId());

        UserEntity userEntity = entity.getUserEntities().iterator().next();
        assertThat(userEntity.getId()).isEqualTo(savedUser.getId());
        assertThat(userEntity.getUsername()).isEqualTo(savedUser.getUsername());
    }

    @Test
    void mapFromDto_shouldThrowExceptionForMissingUserId() {
        // Invalid user ID
        UserDto invalidUserDto = UserDto.builder()
                .id(999L)
                .username("GhostUser")
                .build();

        LobbyDto dto = LobbyDto.builder()
                .lobbyName("Invalid Lobby")
                .isActive(false)
                .isPublic(true)
                .inGame(false)
                .lobbyPlayers(Set.of(invalidUserDto))
                .hostId(invalidUserDto.getId())
                .build();

        assertThatThrownBy(() -> lobbyEntityDtoMapper.mapFromDto(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User id not found: 999");
    }

}
