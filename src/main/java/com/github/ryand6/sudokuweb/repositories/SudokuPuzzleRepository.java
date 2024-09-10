package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SudokuPuzzleRepository extends CrudRepository<SudokuPuzzle, Long> {
}
