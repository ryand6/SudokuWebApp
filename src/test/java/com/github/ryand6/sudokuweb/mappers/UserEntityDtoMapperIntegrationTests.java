package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.UserDto;
import com.github.ryand6.sudokuweb.mappers.Impl.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.ScoreRepository;
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
Integration tests for UserEntityDtoMapper
*/
public class UserEntityDtoMapperIntegrationTests {

    private final ScoreRepository scoreRepository;
    private final UserEntityDtoMapper mapper;
    private final JdbcTemplate jdbcTemplate;

    // Autowired handles mapper dependency injection - required for integration tests
    @Autowired
    public UserEntityDtoMapperIntegrationTests(
            ScoreRepository scoreRepository,
            UserEntityDtoMapper mapper,
            JdbcTemplate jdbcTemplate
    ) {
        this.scoreRepository = scoreRepository;
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    private ScoreEntity savedScore;

    @BeforeEach
    public void setUp() {
        // Correct SQL syntax for deleting all rows from the tables
        jdbcTemplate.execute("DELETE FROM lobby_users");
        jdbcTemplate.execute("DELETE FROM lobby_state");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM scores");
        jdbcTemplate.execute("DELETE FROM sudoku_puzzles");
        jdbcTemplate.execute("DELETE FROM lobbies");
        // Setup test score data in the test DB
        savedScore = ScoreEntity.builder()
                .totalScore(100)
                .gamesPlayed(5)
                .build();
        scoreRepository.save(savedScore);
    }

    @Test
    void mapToDto_shouldReturnValidUserDto() {
        UserEntity userEntity = UserEntity.builder()
                .id(123L)
                .username("testuser")
                .isOnline(true)
                .scoreEntity(savedScore)
                .passwordHash("iiugh3")
                .build();

        UserDto dto = mapper.mapToDto(userEntity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(123L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getIsOnline()).isTrue();
        assertThat(dto.getScoreId()).isEqualTo(savedScore.getId());
    }

    @Test
    void mapFromDto_withPasswordHash_shouldReturnValidUserEntity() {
        UserDto dto = UserDto.builder()
                .id(2L)
                .username("mapperuser")
                .isOnline(true)
                .scoreId(savedScore.getId())
                .build();

        String passwordHash = "password";

        UserEntity entity = mapper.mapFromDto(dto, passwordHash);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getUsername()).isEqualTo("mapperuser");
        assertThat(entity.getPasswordHash()).isEqualTo(passwordHash);
        assertThat(entity.getIsOnline()).isTrue();
        assertThat(entity.getScoreEntity()).isEqualTo(savedScore);
    }

    @Test
    void mapFromDto_shouldThrowException_whenScoreNotFound() {
        UserDto dto = UserDto.builder()
                .username("missingScoreUser")
                .isOnline(true)
                .scoreId(99999L) // ID that does not exist
                .build();

        assertThatThrownBy(() -> mapper.mapFromDto(dto, "dummyPass"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Score not found");
    }

    @Test
    void mapFromDto_withoutPasswordHash_shouldThrowUnsupportedOperationException() {
        UserDto dto = UserDto.builder()
                .id(3L)
                .username("noPassUser")
                .isOnline(true)
                .scoreId(savedScore.getId())
                .build();

        assertThatThrownBy(() -> mapper.mapFromDto(dto))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Password hash required");
    }

}
