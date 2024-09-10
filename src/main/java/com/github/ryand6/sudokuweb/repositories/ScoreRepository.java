package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.Score;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends CrudRepository<Score, Long> {
}
