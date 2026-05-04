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

    private Long difficultyMultiplier;

    private long scoreTimesDifficultyMultiplier;

    private Long timerMultiplier;

    private long scoreTimesTimerMultiplier;

    private int finalScore;

}
