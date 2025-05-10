package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    Iterable<Score> totalScoreLessThan(int totalScore);

}
