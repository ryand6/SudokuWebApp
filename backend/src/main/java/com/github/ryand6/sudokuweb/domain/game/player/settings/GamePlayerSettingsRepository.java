package com.github.ryand6.sudokuweb.domain.game.player.settings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePlayerSettingsRepository extends JpaRepository<GamePlayerSettingsEntity, Long> {
}
