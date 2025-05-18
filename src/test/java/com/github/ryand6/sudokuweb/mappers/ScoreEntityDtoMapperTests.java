package com.github.ryand6.sudokuweb.mappers;

import com.github.ryand6.sudokuweb.TestDataUtil;
import com.github.ryand6.sudokuweb.domain.ScoreEntity;
import com.github.ryand6.sudokuweb.dto.ScoreDto;
import com.github.ryand6.sudokuweb.mappers.Impl.ScoreEntityDtoMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
Unit tests for ScoreEntityDtoMapper
*/
public class ScoreEntityDtoMapperTests {

    private final ScoreEntityDtoMapper scoreEntityDtoMapper = new ScoreEntityDtoMapper();

    @Test
    void mapToDto_shouldReturnValidScoreDto() {
        ScoreEntity scoreEntity = TestDataUtil.createTestScoreA();
        scoreEntity.setId(2L);

        ScoreDto scoreDto = scoreEntityDtoMapper.mapToDto(scoreEntity);

        System.out.println(scoreDto.getId());

        assertThat(scoreDto).isNotNull();
        assertThat(scoreDto.getId()).isEqualTo(2L);
        assertThat(scoreDto.getTotalScore()).isEqualTo(150);
        assertThat(scoreDto.getGamesPlayed()).isEqualTo(1);
    }

    @Test
    void mapFromDto_shouldReturnValidScoreEntity() {
        ScoreDto scoreDto = ScoreDto.builder()
                .id(1L)
                .totalScore(230)
                .gamesPlayed(3)
                .build();

        ScoreEntity scoreEntity = scoreEntityDtoMapper.mapFromDto(scoreDto);

        assertThat(scoreEntity).isNotNull();
        assertThat(scoreEntity.getId()).isEqualTo(1L);
        assertThat(scoreEntity.getTotalScore()).isEqualTo(230);
        assertThat(scoreEntity.getGamesPlayed()).isEqualTo(3);
    }

}
