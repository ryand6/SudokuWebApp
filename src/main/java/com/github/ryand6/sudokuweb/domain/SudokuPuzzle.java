package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sudoku_puzzles")
public class SudokuPuzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "puzzle_id_seq")
    private Long id;

    @Column(name = "initial_board_state", nullable = false)
    private String initialBoardState;

    @Column(name = "solution", nullable = false)
    private String solution;

    @Column(name = "difficulty", nullable = false, columnDefinition = "VARCHAR(50) CHECK (difficulty IN ('easy', 'medium', 'hard', 'extreme'))")
    private String difficulty;

    @OneToMany(mappedBy = "puzzle", orphanRemoval = true)
    private Set<LobbyState> lobbyStates;

}
