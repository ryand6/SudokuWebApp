package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobby_state")
public class LobbyStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lobby_id", nullable = false)
    private LobbyEntity lobbyEntity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "puzzle_id", nullable = false)
    private SudokuPuzzleEntity puzzle;

    @Column(name = "current_board_state", nullable = false)
    private String currentBoardState;

    @Column(name = "score", columnDefinition = "INTEGER DEFAULT 0")
    private Integer score;

    @UpdateTimestamp
    @Column(name = "last_active")
    private LocalDateTime lastActive;

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities e.g. Lobby Users
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyStateEntity lobbyStateEntity = (LobbyStateEntity) o;
        return id != null && id.equals(lobbyStateEntity.id);
    }

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities e.g. Lobby Users
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
