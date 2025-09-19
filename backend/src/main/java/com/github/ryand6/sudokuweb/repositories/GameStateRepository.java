package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.GameStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStateRepository extends JpaRepository<GameStateEntity, Long> {

    // Check is a user is a member of a game via the gameState entity
    boolean existsByUserEntityIdAndGameEntityId(Long userId, Long gameId);

}
