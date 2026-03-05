package com.github.ryand6.sudokuweb.dto.entity.user;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatsDto {

    private int totalScore;

    private int gamesPlayed;

    private int wins;

    private int losses;

    private int draws;

    private int currentWinStreak;

    private int maxWinStreak;

}
