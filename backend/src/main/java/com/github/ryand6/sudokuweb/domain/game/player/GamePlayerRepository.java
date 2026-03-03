package com.github.ryand6.sudokuweb.domain.game.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayerEntity, Long> {

    // Check is a user is a member of a game via the gameState entity
    boolean existsByUserEntityIdAndGameEntityId(Long userId, Long gameId);

}
