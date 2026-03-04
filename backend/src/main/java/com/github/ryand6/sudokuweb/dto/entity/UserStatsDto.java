package com.github.ryand6.sudokuweb.dto.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserStatsDto {

    private Long id;

    private int totalScore;

    private int gamesPlayed;

    private int wins;

    private int losses;

    private int draws;

    private int currentWinStreak;

    private int maxWinStreak;

}
