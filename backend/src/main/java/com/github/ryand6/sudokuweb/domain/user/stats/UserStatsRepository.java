package com.github.ryand6.sudokuweb.domain.user.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatsRepository extends JpaRepository<UserStatsEntity, Long> {

    Iterable<UserStatsEntity> totalScoreLessThan(int totalScore);

}
