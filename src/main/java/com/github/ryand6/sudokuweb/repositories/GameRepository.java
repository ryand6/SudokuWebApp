package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {
}
