package com.github.ryand6.sudokuweb.util;

import java.util.Map;

public class ScoringTables {

    // STANDARD GAME MODE SCORING TABLES

    public static final int standardGameMode_PenaltyMultiplierCap = 3;

    // Penalties for number of mistakes made per cell
    public static final Map<Integer, Integer> standardGameMode_Penalties = Map.of(
            1, -1,
            2, -3,
            standardGameMode_PenaltyMultiplierCap, -5
    );

    // Key = claim position
    public static final Map<Integer, Integer> standardGameMode_PointsPerClaimPosition = Map.of(
            -1, 1,
            1, 4,
            2, 3,
            3, 2,
            4, 1
    );

    public static final int standardGameMode_StreakBonusCap = 4;

    public static final Map<Integer, Integer> standardGameMode_BonusPointsPerStreak = Map.of(
            2, 1,
            3, 2,
            standardGameMode_StreakBonusCap, 3
    );

    // DOMINATION GAME MODE SCORING TABLES

    public static final int dominationGameMode_BaseScore = 10;

    public static final int dominationGameMode_BasePenalty = -8;

    public static final int dominationGameMode_StreakBonusCap = 4;

    public static final Map<Integer, Integer> dominationGameMode_BonusPointsPerStreak = Map.of(
            2, 2,
            3, 3,
            dominationGameMode_StreakBonusCap, 5
    );

    // TIME ATTACK GAME MODE SCORING TABLES

    public static final int timeAttackGameMode_BaseScore = 20;

    public static final int timeAttackGameMode_AddedSecondsOnCorrectAnswer = 5;

    public static final int timeAttackGameMode_RemovedSecondsOnIncorrectAnswer = 8;

}
