package com.github.ryand6.sudokuweb.mappers.Impl.leaderboards;

import com.github.ryand6.sudokuweb.domain.leaderboards.LeaderboardsEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.dto.entity.leaderboards.LeaderboardsDto;
import com.github.ryand6.sudokuweb.mappers.EntityDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardsEntityDtoMapper implements EntityDtoMapper<LeaderboardsEntity, LeaderboardsDto> {

    @Override
    public LeaderboardsDto mapToDto(LeaderboardsEntity leaderboardsEntity) {
        UserEntity user = leaderboardsEntity.getUserEntity();

        return LeaderboardsDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .gameMode(leaderboardsEntity.getGameMode())
                .totalScore(leaderboardsEntity.getTotalScore())
                .gamesPlayed(leaderboardsEntity.getGamesPlayed())
                .wins(leaderboardsEntity.getWins())
                .losses(leaderboardsEntity.getLosses())
                .draws(leaderboardsEntity.getDraws())
                .currentWinStreak(leaderboardsEntity.getCurrentWinStreak())
                .build();
    }

}
