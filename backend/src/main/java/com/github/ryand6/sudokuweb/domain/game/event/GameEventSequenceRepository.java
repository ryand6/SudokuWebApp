package com.github.ryand6.sudokuweb.domain.game.event;

import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface GameEventSequenceRepository extends JpaRepository<GameEventSequenceEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM GameEventSequenceEntity s WHERE s.gameId = :gameId")
    Optional<GameEventSequenceEntity> findByGameIdForUpdate(@Param("gameId") Long gameId);

}
