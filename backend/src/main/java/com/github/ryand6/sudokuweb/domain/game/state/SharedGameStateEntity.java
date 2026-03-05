package com.github.ryand6.sudokuweb.domain.game.state;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
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
@Table(name = "shared_game_states")
public class SharedGameStateEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "game_id", nullable = false, unique = true)
    private GameEntity gameEntity;

    @Column(name = "current_shared_board_state", nullable = false)
    private String currentSharedBoardState;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

}
