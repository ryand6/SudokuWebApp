package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "lobby_state")
public class LobbyState {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "state_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lobby_id", nullable = false)
    private Lobby lobby;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "puzzle_id", nullable = false)
    private SudokuPuzzle puzzle;

    @Column(name = "current_board_state", nullable = false)
    private String currentBoardState;

    @Column(name = "score", columnDefinition = "INTEGER DEFAULT 0")
    private Integer score;

    @UpdateTimestamp
    @Column(name = "last_active")
    private LocalDateTime lastActive;

}
