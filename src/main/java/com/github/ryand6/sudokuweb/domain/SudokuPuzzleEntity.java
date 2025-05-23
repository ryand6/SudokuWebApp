package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sudoku_puzzles")
public class SudokuPuzzleEntity {

    public enum Difficulty {
        EASY, MEDIUM, HARD, EXTREME
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "puzzle_id_seq")
    private Long id;

    @Column(name = "initial_board_state", nullable = false)
    private String initialBoardState;

    @Column(name = "solution", nullable = false)
    private String solution;

    @Column(name = "difficulty", nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @OneToMany(mappedBy = "sudokuPuzzleEntity", cascade = CascadeType.ALL)
    private Set<LobbyStateEntity> lobbyStateEntities;

    // Overwrite to prevent circular referencing/lazy loading of referenced entities e.g. LobbyState
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SudokuPuzzleEntity sudokuPuzzleEntity = (SudokuPuzzleEntity) o;
        return id != null && id.equals(sudokuPuzzleEntity.id);
    }

    // Overwrite to prevent circular referencing/lazy loading of referenced entities e.g. LobbyState
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
