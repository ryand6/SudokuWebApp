package com.github.ryand6.sudokuweb.mappers.Impl;

import com.github.ryand6.sudokuweb.domain.user.stats.UserStatsEntity;
import com.github.ryand6.sudokuweb.dto.entity.ScoreDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class ScoreEntityDtoMapper implements EntityDtoMapper<UserStatsEntity, ScoreDto> {

    @Override
    public ScoreDto mapToDto(UserStatsEntity userStatsEntity) {
        return ScoreDto.builder()
                .id(userStatsEntity.getId())
                .totalScore(userStatsEntity.getTotalScore())
                .gamesPlayed(userStatsEntity.getGamesPlayed())
                .build();
    }

}
