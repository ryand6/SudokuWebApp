package com.github.ryand6.sudokuweb.domain.leaderboards;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.GameMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "leaderboards",
        indexes = {
                @Index(name = "idx_game_mode_total_score_desc", columnList = "game_mode, total_score DESC")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_user_game_mode", columnNames = {"user_id", "game_mode"})
        }
)
public class LeaderboardsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leaderboards_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "game_mode")
    private GameMode gameMode;

    @Column(name = "total_score")
    private long totalScore = 0;

    @Column(name = "games_played")
    private int gamesPlayed = 0;

    @Column(name = "wins")
    private int wins = 0;

    @Column(name = "losses")
    private int losses = 0;

    @Column(name = "draws")
    private int draws = 0;

    @Column(name = "current_win_streak")
    private int currentWinStreak = 0;

    @Column(name = "max_win_streak")
    private int maxWinStreak = 0;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    //#######################//
    // Domain Business Logic //
    //#######################//

    public void recordWin() {
        wins++;
        gamesPlayed++;
        currentWinStreak++;
        maxWinStreak = Math.max(maxWinStreak, currentWinStreak);
    }

    public void recordLoss() {
        losses++;
        gamesPlayed++;
        currentWinStreak = 0;
    }

    public void recordDraw() {
        draws++;
        gamesPlayed++;
        currentWinStreak = 0;
    }

    public void applyScore(Integer score) {
        if (score == null) {
            score = 0;
        }
        totalScore += score;
    }

}
