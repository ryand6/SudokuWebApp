package com.github.ryand6.sudokuweb.domain.game.player;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.settings.GameSettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameResult;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import com.github.ryand6.sudokuweb.enums.PlayerColour;
import jakarta.persistence.*;
import lombok.*;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_players")
public class GamePlayerEntity {

    @EmbeddedId
    private GamePlayerId id = new GamePlayerId();

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity gameEntity;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @OneToOne(mappedBy = "gamePlayerEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private GamePlayerStateEntity gamePlayerStateEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_colour", nullable = false)
    private PlayerColour playerColour;

    @Column(name = "score")
    private int score = 0;

    // Counts the number of times a player answered a cell without making a mistake before any other player
    @Column(name = "firsts")
    private int firsts = 0;

    @Column(name = "mistakes")
    private int mistakes = 0;

    @Column(name = "max_streak", nullable = false)
    private int maxStreak = 0;

    @Column(name = "game_loaded")
    private boolean gameLoaded = false;

    @Column(name = "game_loaded_timestamp")
    private Instant gameLoadedTimestamp = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_result", nullable = false)
    private GameResult gameResult = GameResult.PENDING;

    // Stored the UTC timestamp of the last submitted chat message by the player - used to prevent message spamming
    @Column(name = "game_message_timestamp")
    private Instant lastGameMessageTimestamp = null;

    // Determines when game results should be displayed to the player upon completion / game ending
    @Column(name = "finished_game")
    private boolean finishedGame = false;

    @Column(name = "finished_game_timestamp")
    private Instant finishedGameTimestamp = null;

    // Score applied to leaderboard once player finishes game
    @Column(name = "leaderboard_score")
    private Integer leaderboardScore = null;

    @Version
    private Long version;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GamePlayerEntity gamePlayerEntity)) return false;
        // Compare using the two linked entities that make up the unique identifier
        return Objects.equals(gameEntity, gamePlayerEntity.getGameEntity()) &&
                Objects.equals(userEntity, gamePlayerEntity.getUserEntity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameEntity, userEntity);
    }

    //#######################//
    // Domain Business Logic //
    //#######################//

    public boolean canGameResultBeUpdated() {
        return gameEntity.getGameStatus() == GameStatus.FINISHED;
    }

    public void markGameLoaded() {
        if (!gameLoaded && gameLoadedTimestamp == null) {
            gameLoaded = true;
            gameLoadedTimestamp = Instant.now();
        }
    }

    public void markGameFinished() {
        finishedGame = true;
        finishedGameTimestamp = Instant.now();
    }

    public void incrementFirsts() {
        firsts += 1;
    }

    public void incrementMistakes() {
        mistakes += 1;
    }

    public void updateScore(int scoreUpdate) {
        score += scoreUpdate;
    }

    public void setMaxStreak(int currentStreak) {
        maxStreak = currentStreak > maxStreak ? currentStreak : maxStreak;
    }

    public void updateLastGameMessageTime() {
        lastGameMessageTimestamp = Instant.now();
    }

    public LeaderboardScoreCalculation calculateLeaderboardScore() {
        int baseScore = score;
        int completedCellsCount;
        GameMode gameMode = gameEntity.getGameSettingsEntity().getGameMode();
        // UPDATE - consider splitting TimeAttack and Domination, since TimeAttack should take into account cells completed by all players
        if (gameMode == GameMode.DOMINATION) {
            completedCellsCount = gameEntity.getSharedGameStateEntity().getPlayerCompletedCellsCount_SharedGameMode(userEntity.getId());
        } else if (gameMode == GameMode.TIMEATTACK) {
            completedCellsCount = gameEntity.getSharedGameStateEntity().getNumberOfCompletedCells();
        } else {
            completedCellsCount = gamePlayerStateEntity.getNumberOfCompletedCells();
        }
        int cellsGiven = gameEntity.getSudokuPuzzleEntity().getNumberOfCellsGiven();
        int cellsToFill = 81 - cellsGiven;
        long scoreOverCellsCompleted = baseScore != 0 ? baseScore / completedCellsCount : 0;
        int normalisationRate = Math.round(((float) completedCellsCount / cellsToFill) * 100);
        long normalisedScore = scoreOverCellsCompleted * normalisationRate;

        GameSettingsEntity gameSettings = gameEntity.getGameSettingsEntity();

        Double difficultyMultiplier = gameSettings.getDifficultyMultiplier();
        Double scoreTimesDifficultyMultiplier = normalisedScore * difficultyMultiplier;
        Double timerMultiplier;
        if (gameMode == GameMode.TIMEATTACK) {
            timerMultiplier = calculateTimeAttackMultiplier();
        } else {
            timerMultiplier = gameSettings.getTimerMultiplier();
        }
        Double scoreTimesTimerMultiplier = scoreTimesDifficultyMultiplier * timerMultiplier;
        int finalScore = scoreTimesTimerMultiplier.intValue();
        return new LeaderboardScoreCalculation(
                baseScore,
                completedCellsCount,
                scoreOverCellsCompleted,
                normalisationRate,
                normalisedScore,
                difficultyMultiplier,
                scoreTimesDifficultyMultiplier,
                timerMultiplier,
                scoreTimesTimerMultiplier,
                finalScore);
    }

    double calculateTimeAttackMultiplier() {
        int baseTimer = gameEntity.getSharedGameStateEntity().getTimeAttackBaseTimer();
        Duration difference = Duration.between(gameEntity.getGameEndsAt(), gameEntity.getGameStartsAt());
        long timerAtFinish = difference.getSeconds();
        if (timerAtFinish < 0) {
            // If the game has already finished, set the multiplier to 1.0x
            return 1;
        }
        double percentagePassed = (double) timerAtFinish / baseTimer;
        // Map the percentage to a multiplier
        double multiplier = 1.0 + percentagePassed;

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);

        // Return the calculated multiplier, rounded to two decimal places
        String formattedNumber = df.format(multiplier);
        return Double.parseDouble(formattedNumber);
    }

}
