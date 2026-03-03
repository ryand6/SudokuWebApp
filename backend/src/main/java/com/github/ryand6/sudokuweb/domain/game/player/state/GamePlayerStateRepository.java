package com.github.ryand6.sudokuweb.domain.game.player.state;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePlayerStateRepository extends JpaRepository<GamePlayerStateEntity, Long> {

}
