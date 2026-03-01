package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.puzzle.SudokuPuzzleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SudokuPuzzleRepository extends JpaRepository<SudokuPuzzleEntity, Long> {

    boolean existsByInitialBoardStateAndSolution(String initialBoardState, String solution);

    SudokuPuzzleEntity findByInitialBoardStateAndSolution(String initialBoardState, String solution);

}
