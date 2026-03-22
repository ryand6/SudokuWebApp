package com.github.ryand6.sudokuweb.domain.puzzle;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.exceptions.game.state.IllegalBoardStateException;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode
@Table(name = "sudoku_puzzles")
public class SudokuPuzzleEntity {

    public static final int NUMBER_OF_CELLS = 81;

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

    //#######################//
    // Domain Business Logic //
    //#######################//

    public static void verifyBoardStateAndSolutionLength(String initialBoardState, String solution) {
        if (initialBoardState == null || solution == null) {
            throw new IllegalBoardStateException("Board cannot be null");
        }
        if (initialBoardState.length() != NUMBER_OF_CELLS || solution.length() != NUMBER_OF_CELLS) {
            throw new IllegalBoardStateException("Board does not consist of 81 cells");
        }
    }

    public int getNumberOfCellsToFill() {
        return initialBoardState.length() - initialBoardState.replace(".", "").length();
    }

}
