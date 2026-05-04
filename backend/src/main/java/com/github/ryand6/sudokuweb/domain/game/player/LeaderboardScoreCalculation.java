package com.github.ryand6.sudokuweb.domain.game.player;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LeaderboardScoreCalculation {

    private int score;

    private int cellsCompleted;

    private long scoreOverCellsCompleted;

    private int normalisationRate;

    private long normalisedScore;

    private Double difficultyMultiplier;

    private Double scoreTimesDifficultyMultiplier;

    private Double timerMultiplier;

    private Double scoreTimesTimerMultiplier;

    private int finalScore;

}
