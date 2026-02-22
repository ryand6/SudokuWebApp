package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.GameEventEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameEventRepository extends JpaRepository<GameEventEntity, Long> {

    @Query(value = """
            SELECT MAX(ge.sequence_number, 0)
            FROM GameEventEntity ge
            WHERE ge.gameEntity.id = :gameId
            """)
    Long getMaxSequenceNumber(@Param("gameId") Long gameId);

}
