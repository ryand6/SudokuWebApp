package com.github.ryand6.sudokuweb.domain.leaderboards;

import com.github.ryand6.sudokuweb.enums.GameMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardsRepository extends JpaRepository<LeaderboardsEntity, Long> {

    Page<LeaderboardsEntity> findByGameModeOrderByTotalScoreDesc(GameMode gameMode, Pageable pageable);

    Optional<LeaderboardsEntity> findByGameModeAndUserEntity_Id(GameMode gameMode, Long userId);

    @Query(
            value = """
            SELECT user_rank, user_id, username, total_score FROM (
                SELECT u.id AS user_id,
                       RANK() OVER (ORDER BY l.total_score DESC) AS user_rank,
                       u.username AS username,
                       l.total_score AS total_score,
                       l.game_mode AS game_mode
                FROM users u
                JOIN leaderboards l ON u.id = l.user_id
                WHERE l.game_mode = :gameMode
            ) ranked
            WHERE user_rank <= 10 OR user_id = :userId
            ORDER BY user_rank
        """, nativeQuery = true)
    List<TopTenLeaderboardRow> findTop10WithUserRank(@Param("userId") Long userId, @Param("gameMode") String gameMode);

}
