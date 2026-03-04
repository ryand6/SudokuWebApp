package com.github.ryand6.sudokuweb.domain.user.stats;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_stats")
public class UserStatsEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity userEntity;

    @Column(name = "total_score")
    private int totalScore = 0;

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

}
