package com.github.ryand6.sudokuweb.domain;

import com.github.ryand6.sudokuweb.enums.PlayerColour;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "game_state")
public class GameStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity gameEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "current_board_state", nullable = false)
    private String currentBoardState;

    @Column(name = "score", columnDefinition = "INTEGER DEFAULT 0")
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(name = "player_colour", nullable = false)
    private PlayerColour playerColour;

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities e.g. Lobby Users
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStateEntity gameStateEntity = (GameStateEntity) o;
        return id != null && id.equals(gameStateEntity.id);
    }

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities e.g. Lobby Users
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
