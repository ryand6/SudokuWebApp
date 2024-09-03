package com.github.ryand6.sudokuweb.dao;

import com.github.ryand6.sudokuweb.domain.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreDao {

    void create(Score score);

    Optional<Score> findOne(Long scoreId);

    List<Score> find();

    void update(Long scoreId, Score score);

    void delete(Long scoreId);

}
