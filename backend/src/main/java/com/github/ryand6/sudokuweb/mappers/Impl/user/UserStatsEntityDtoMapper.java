package com.github.ryand6.sudokuweb.mappers.Impl.user;

import com.github.ryand6.sudokuweb.domain.user.stats.UserStatsEntity;
import com.github.ryand6.sudokuweb.dto.entity.user.UserStatsDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserStatsEntityDtoMapper implements EntityDtoMapper<UserStatsEntity, UserStatsDto> {

    @Override
    public UserStatsDto mapToDto(UserStatsEntity userStatsEntity) {
        return UserStatsDto.builder()
                .totalScore(userStatsEntity.getTotalScore())
                .gamesPlayed(userStatsEntity.getGamesPlayed())
                .wins(userStatsEntity.getWins())
                .losses(userStatsEntity.getLosses())
                .draws(userStatsEntity.getDraws())
                .currentWinStreak(userStatsEntity.getCurrentWinStreak())
                .maxWinStreak(userStatsEntity.getMaxWinStreak())
                .build();
    }

}
