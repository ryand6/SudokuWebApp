package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.score.ScoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntity, Long> {

    Iterable<ScoreEntity> totalScoreLessThan(int totalScore);

}
