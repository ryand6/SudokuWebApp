package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // Returns UserEntity if exists based on combination of OAuth2 provider and provider ID
    Optional<UserEntity> findByProviderAndProviderId(String provider, String providerId);

    // Checks to see if username already taken
    boolean existsByUsername(String username);

    // Returns the top Users ordered be total score descending - number of players to return is
    // defined by Pageable
    Page<UserEntity> findByOrderByScoreEntity_TotalScoreDesc(Pageable pageable);

    // Gets the players rank based on their total_score when compared to all other players
    // Using nativeQuery due to use of window function RANK()
    @Query(
        value = """
            SELECT user_rank FROM (
                SELECT u.id as user_id,
                       RANK() OVER (ORDER BY s.total_score DESC) AS user_rank
                FROM users u
                JOIN scores s ON u.score_id = s.id
            ) ranked
            WHERE user_id = :userId
        """, nativeQuery = true)
    Long getUserRankById(@Param("userId") Long userId);

}
