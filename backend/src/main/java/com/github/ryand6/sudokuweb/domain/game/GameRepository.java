package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.enums.GameStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM GameEntity g WHERE g.id = :id")
    Optional<GameEntity> findByIdWithLock(@Param("id") Long id);

    Set<GameEntity> findByGamePlayerEntities_UserEntity_IdAndGameStatusIn(Long userId, Set<GameStatus> statuses);

}
