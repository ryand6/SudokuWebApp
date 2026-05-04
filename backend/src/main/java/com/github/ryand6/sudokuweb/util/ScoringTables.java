package com.github.ryand6.sudokuweb.util;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;

import java.util.Map;
import java.util.TreeMap;

public final class ScoringTables {

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

    public static final int timeAttackGameMode_TeamMemberSolvedScore = 10;

    public static final int timeAttackGameMode_RemovedSecondsOnIncorrectAnswer = -8;

    public static final Map<Difficulty, Integer> timeAttackGameMode_BaseTimers = Map.of(
            Difficulty.EASY, 60,
            Difficulty.MEDIUM, 50,
            Difficulty.HARD, 40,
            Difficulty.EXTREME, 30
    );

    public static final Map<Difficulty, Integer> timeAttackGameMode_CellsToSolveMultiplier = Map.of(
            Difficulty.EASY, 5,
            Difficulty.MEDIUM, 6,
            Difficulty.HARD, 7,
            Difficulty.EXTREME, 8
    );

    public static final Map<Integer, Integer> timeAttackGameMode_TeamSizeTimerBonus = Map.of(
            2, 20,
            3, 10,
            4, 0
    );

    public static final TreeMap<Integer, Integer> timeAttackGameMode_AddedSecondsOnCorrectAnswer_ByPercentageOfCellsLeft = new TreeMap<>();

    static {
        timeAttackGameMode_AddedSecondsOnCorrectAnswer_ByPercentageOfCellsLeft.put(80, 5);
        timeAttackGameMode_AddedSecondsOnCorrectAnswer_ByPercentageOfCellsLeft.put(60, 9);
        timeAttackGameMode_AddedSecondsOnCorrectAnswer_ByPercentageOfCellsLeft.put(40, 7);
        timeAttackGameMode_AddedSecondsOnCorrectAnswer_ByPercentageOfCellsLeft.put(20, 5);
        timeAttackGameMode_AddedSecondsOnCorrectAnswer_ByPercentageOfCellsLeft.put(0, 3);
    }

    // DIFFICULTY SCORE MULTIPLIER

    public static final Map<Difficulty, Double> difficultyMultiplier = Map.of(
            Difficulty.EASY, 1.0,
            Difficulty.MEDIUM, 1.1,
            Difficulty.HARD, 1.2,
            Difficulty.EXTREME, 1.3
    );

    // TIME LIMIT PRESET MULTIPLIER
    public static final Map<TimeLimitPreset, Double> timeLimitPresetMultiplier = Map.of(
            TimeLimitPreset.UNLIMITED, 1.0,
            TimeLimitPreset.MARATHON, 1.0,
            TimeLimitPreset.STANDARD, 1.1,
            TimeLimitPreset.QUICK, 1.2
    );

}
