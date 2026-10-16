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

    private double scoreOverCellsCompleted;

    private int normalisationRate;

    private double normalisedScore;

    private double difficultyMultiplier;

    private double scoreTimesDifficultyMultiplier;

    private double timerMultiplier;

    private double scoreTimesTimerMultiplier;

    private int finalScore;

}
