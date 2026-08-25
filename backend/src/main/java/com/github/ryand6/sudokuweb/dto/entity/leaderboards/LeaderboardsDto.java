package com.github.ryand6.sudokuweb.dto.entity.leaderboards;

import com.github.ryand6.sudokuweb.enums.GameMode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaderboardsDto {

    private Long userId;

    private String username;

    private GameMode gameMode;

    private long totalScore;

    private int gamesPlayed;

    private int wins;

    private int losses;

    private int draws;

    private int currentWinStreak;

    private int maxWinStreak;

}
