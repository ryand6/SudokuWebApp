package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.SudokuPuzzleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SudokuPuzzleRepository extends JpaRepository<SudokuPuzzleEntity, Long> {

}
