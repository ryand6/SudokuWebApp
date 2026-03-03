package com.github.ryand6.sudokuweb.domain.game.state;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedGameStateRepository extends JpaRepository<SharedGameStateEntity, Long> {
}
