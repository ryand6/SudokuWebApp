package com.github.ryand6.sudokuweb.domain.game.event;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameEventRepository extends JpaRepository<GameEventEntity, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query(value = """
//        SELECT COALESCE(MAX(ge.sequenceNumber), 0)
//        FROM GameEventEntity ge
//        WHERE ge.gameEntity.id = :gameId
//        """)
//    Long getMaxSequenceNumber(@Param("gameId") Long gameId);

}
