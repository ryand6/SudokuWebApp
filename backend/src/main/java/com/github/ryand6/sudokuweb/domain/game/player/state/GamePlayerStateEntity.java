package com.github.ryand6.sudokuweb.domain.game.player.state;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerId;
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
@Table(name = "game_player_states")
public class GamePlayerStateEntity {

    @EmbeddedId
    private GamePlayerId id; // same composite key as GamePlayerEntity

    @MapsId
    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "game_id", referencedColumnName = "game_id", nullable = false, unique = true),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    })
    private GamePlayerEntity gamePlayerEntity;

    @Column(name = "current_board_state")
    private String currentBoardState = null;

    // Each pair of bytes in the array is a bitmask acting as a binary representation of the notes active for that cell
    @Column(name = "notes", nullable = false)
    private byte[] notes = new byte[81 * 2];

    @Column(name = "current_streak", nullable = false)
    private int currentStreak = 0;

    @Column(name = "active_multiplier", nullable = false)
    private double activeMultiplier = 0;

    @Column(name = "multiplier_ends_at")
    private Instant multiplierEndsAt = null;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePlayerStateEntity gamePlayerStateEntity = (GamePlayerStateEntity) o;
        return id != null && id.equals(gamePlayerStateEntity.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
