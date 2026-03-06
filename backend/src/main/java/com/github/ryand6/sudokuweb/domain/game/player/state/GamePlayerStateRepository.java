package com.github.ryand6.sudokuweb.domain.game.player.state;

import com.github.ryand6.sudokuweb.domain.game.player.GamePlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GamePlayerStateRepository extends JpaRepository<GamePlayerStateEntity, Long> {

    @Query(value = "SELECT * FROM game_player_states WHERE game_id = :gameId AND user_id = :userId", nativeQuery = true)
    Optional<GamePlayerStateEntity> findByCompositeId(@Param("gameId") Long gameId, @Param("userId") Long userId);

}
