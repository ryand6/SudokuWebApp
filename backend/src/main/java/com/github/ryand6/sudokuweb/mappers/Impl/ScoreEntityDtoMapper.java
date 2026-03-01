package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.score.ScoreEntity;
import com.github.ryand6.sudokuweb.dto.entity.ScoreDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class ScoreEntityDtoMapper implements EntityDtoMapper<ScoreEntity, ScoreDto> {

    @Override
    public ScoreDto mapToDto(ScoreEntity scoreEntity) {
        return ScoreDto.builder()
                .id(scoreEntity.getId())
                .totalScore(scoreEntity.getTotalScore())
                .gamesPlayed(scoreEntity.getGamesPlayed())
                .build();
    }

}
