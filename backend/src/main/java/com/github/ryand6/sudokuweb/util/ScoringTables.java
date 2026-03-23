package com.github.ryand6.sudokuweb.util;

import java.util.Map;

public class ScoringTables {

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

}
