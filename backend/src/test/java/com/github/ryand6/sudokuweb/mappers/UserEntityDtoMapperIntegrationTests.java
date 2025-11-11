package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.domain.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.UserDto;
import com.github.ryand6.sudokuweb.integration.AbstractIntegrationTest;
import com.github.ryand6.sudokuweb.mappers.Impl.ScoreEntityDtoMapper;
import com.github.ryand6.sudokuweb.mappers.Impl.UserEntityDtoMapper;
import com.github.ryand6.sudokuweb.repositories.ScoreRepository;
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

/*
Integration tests for UserEntityDtoMapper
*/
public class UserEntityDtoMapperIntegrationTests extends AbstractIntegrationTest {

    private final ScoreRepository scoreRepository;
    private final ScoreEntityDtoMapper scoreEntityDtoMapper;
    private final UserEntityDtoMapper mapper;

    // Autowired handles mapper dependency injection - required for integration tests
    @Autowired
    public UserEntityDtoMapperIntegrationTests(
            ScoreRepository scoreRepository,
            ScoreEntityDtoMapper scoreEntityDtoMapper,
            UserEntityDtoMapper mapper
    ) {
        this.scoreRepository = scoreRepository;
        this.scoreEntityDtoMapper = scoreEntityDtoMapper;
        this.mapper = mapper;
    }

    private ScoreEntity savedScore;

    @BeforeEach
    public void setUp() {
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
                .providerId("iiugh3")
                .build();

        UserDto dto = mapper.mapToDto(userEntity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(123L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getIsOnline()).isTrue();
        assertThat(dto.getScore().getId()).isEqualTo(scoreEntityDtoMapper.mapToDto(savedScore).getId());
    }

//    @Test
//    void mapFromDto_withProviderAndProviderId_shouldReturnValidUserEntity() {
//        UserDto dto = UserDto.builder()
//                .id(2L)
//                .username("mapperuser")
//                .isOnline(true)
//                .score(scoreEntityDtoMapper.mapToDto(savedScore))
//                .build();
//
//        String provider = "dummyProvider";
//        String providerId = "dummyProviderId";
//
//        UserEntity entity = mapper.mapFromDto(dto, provider, providerId);
//
//        assertThat(entity).isNotNull();
//        assertThat(entity.getId()).isEqualTo(2L);
//        assertThat(entity.getUsername()).isEqualTo("mapperuser");
//        assertThat(entity.getProvider()).isEqualTo(provider);
//        assertThat(entity.getProviderId()).isEqualTo(providerId);
//        assertThat(entity.getIsOnline()).isTrue();
//        assertThat(entity.getScoreEntity().getId()).isEqualTo(savedScore.getId());
//    }
//
//    @Test
//    void mapFromDto_withoutProviderId_shouldThrowUnsupportedOperationException() {
//        UserDto dto = UserDto.builder()
//                .id(3L)
//                .username("noPassUser")
//                .isOnline(true)
//                .score(scoreEntityDtoMapper.mapToDto(savedScore))
//                .build();
//
//        assertThatThrownBy(() -> mapper.mapFromDto(dto))
//                .isInstanceOf(UnsupportedOperationException.class)
//                .hasMessageContaining("Provider and Provider ID required");
//    }

}
