package com.github.ryand6.sudokuweb.domain.score;

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
@Table(name = "scores")
public class ScoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "score_id_seq")
    private Long id;

    @Column(name = "total_score", columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalScore;

    @Column(name = "games_played", columnDefinition = "INTEGER DEFAULT 0")
    private Integer gamesPlayed;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

}
