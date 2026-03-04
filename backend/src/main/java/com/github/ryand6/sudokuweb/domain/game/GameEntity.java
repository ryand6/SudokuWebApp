package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import com.github.ryand6.sudokuweb.domain.game.player.state.GamePlayerStateEntity;
import com.github.ryand6.sudokuweb.domain.game.state.SharedGameStateEntity;
import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "games")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lobby_id", nullable = false)
    private LobbyEntity lobbyEntity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "puzzle_id", nullable = false)
    private SudokuPuzzleEntity sudokuPuzzleEntity;

    @OneToMany(mappedBy = "gameEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GamePlayerEntity> gamePlayerEntities;

    @OneToOne(mappedBy = "gameEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private SharedGameStateEntity sharedGameStateEntity;

    @Column(name = "mode")
    @Enumerated(EnumType.STRING)
    private GameMode gameMode = GameMode.CLASSIC;

    @Column(name = "game_status")
    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus = GameStatus.IN_PROGRESS;

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameEntity gameEntity = (GameEntity) o;
        return id != null && id.equals(gameEntity.id);
    }

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
