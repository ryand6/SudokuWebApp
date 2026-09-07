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

//    Page<LeaderboardsEntity> findByGameModeOrderByRank(GameMode gameMode, Pageable pageable);

    @Query(
            value = """
                SELECT rank, user_id, username, total_score, games_played, wins, losses, draws, max_win_streak FROM (
                    SELECT u.id AS user_id,
                           DENSE_RANK() OVER (ORDER BY l.total_score DESC) AS rank,
                           u.username AS username,
                           l.total_score AS total_score,
                           l.games_played AS games_played,
                           l.wins AS wins,
                           l.losses AS losses,
                           l.draws AS draws,
                           l.max_win_streak AS max_win_streak
                    FROM users u
                    JOIN leaderboards l ON u.id = l.user_id
                    WHERE l.game_mode = :gameMode
                ) ranked
                ORDER BY rank
            """,
            countQuery = "SELECT COUNT(*) FROM leaderboards l WHERE l.game_mode = :gameMode",
            nativeQuery = true)
    Page<LeaderboardRow> findByGameModeOrderByRank(@Param("gameMode") String gameMode, Pageable pageable);

    @Query(
            value = """
                SELECT rank, user_id, username, total_score, games_played, wins, losses, draws, max_win_streak FROM (
                    SELECT u.id AS user_id,
                           DENSE_RANK() OVER (ORDER BY l.total_score DESC) AS rank,
                           u.username AS username,
                           l.total_score AS total_score,
                           l.games_played AS games_played,
                           l.wins AS wins,
                           l.losses AS losses,
                           l.draws AS draws,
                           l.max_win_streak AS max_win_streak
                    FROM users u
                    JOIN leaderboards l ON u.id = l.user_id
                    WHERE l.game_mode = :gameMode
                ) ranked
                WHERE user_id = :userId
            """,
            nativeQuery = true
    )
    Optional<LeaderboardRow> findUserLeaderboardRow(@Param("userId") Long userId, @Param("gameMode") String gameMode);

    Optional<LeaderboardsEntity> findByGameModeAndUserEntity_Id(GameMode gameMode, Long userId);

    @Query(
            value = """
            SELECT rank, user_id, username, total_score FROM (
                SELECT u.id AS user_id,
                       DENSE_RANK() OVER (ORDER BY l.total_score DESC) AS rank,
                       u.username AS username,
                       l.total_score AS total_score
                FROM users u
                JOIN leaderboards l ON u.id = l.user_id
                WHERE l.game_mode = :gameMode
            ) ranked
            WHERE rank <= 5 OR user_id = :userId
            ORDER BY rank
        """, nativeQuery = true)
    List<TopFiveLeaderboardRow> findTopFiveWithUserRank(@Param("userId") Long userId, @Param("gameMode") String gameMode);

    @Query(
            value = """
                    SELECT rank FROM (
                        SELECT l.user_id AS user_id,
                               DENSE_RANK() OVER (ORDER BY l.total_score DESC) AS rank
                        FROM leaderboards l
                        WHERE l.game_mode = :gameMode
                    ) ranked
                    WHERE user_id = :userId
        """, nativeQuery = true)
    Optional<Long> findUserGameModeRank(@Param("userId") Long userId, @Param("gameMode") String gameMode);

}
