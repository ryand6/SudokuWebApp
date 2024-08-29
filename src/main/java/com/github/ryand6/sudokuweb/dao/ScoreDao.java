package com.github.ryand6.sudokuweb.dao;

import com.github.ryand6.sudokuweb.domain.Score;

import java.util.Optional;

public interface ScoreDao {

    void create(Score score);

    Optional<Score> findOne(Long scoreId);

}
