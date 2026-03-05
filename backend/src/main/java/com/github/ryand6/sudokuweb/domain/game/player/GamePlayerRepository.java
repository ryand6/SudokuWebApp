package com.github.ryand6.sudokuweb.domain.game.player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamePlayerRepository extends JpaRepository<GamePlayerEntity, GamePlayerId> {

    @Query(value = "SELECT * FROM game_players WHERE game_id = :gameId AND user_id = :userId", nativeQuery = true)
    Optional<GamePlayerEntity> findByCompositeId(@Param("gameId") Long gameId, @Param("userId") Long userId);

    boolean existsByUserEntity_IdAndGameEntity_Id(Long userId, Long gameId);
}
