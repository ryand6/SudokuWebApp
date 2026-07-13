package com.github.ryand6.sudokuweb.domain.leaderboards;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardsRepository extends JpaRepository<LeaderboardsEntity, Long> {



}
