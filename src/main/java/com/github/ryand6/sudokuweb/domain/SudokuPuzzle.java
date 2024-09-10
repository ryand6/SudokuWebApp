package com.github.ryand6.sudokuweb.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sudoku_puzzles")
public class SudokuPuzzle {

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

//    @OneToMany(mappedBy = "puzzle")
//    private Set<LobbyState> lobbyStates;

}
