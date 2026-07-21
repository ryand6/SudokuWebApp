package com.github.ryand6.sudokuweb.domain.leaderboards;

import com.github.ryand6.sudokuweb.enums.GameMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaderboardsRepository extends JpaRepository<LeaderboardsEntity, Long> {

    Page<LeaderboardsEntity> findByGameModeOrderByTotalScoreDesc(GameMode gameMode, Pageable pageable);

    Optional<LeaderboardsEntity> findByGameModeAndUserEntity_Id(GameMode gameMode, Long userId);

}
