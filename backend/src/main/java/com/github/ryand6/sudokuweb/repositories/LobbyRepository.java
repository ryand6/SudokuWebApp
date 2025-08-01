package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyRepository extends JpaRepository<LobbyEntity, Long> {

    // Returns page containing list of active public lobbies within the pageable size range
    Page<LobbyEntity> findByIsPublicTrueAndIsActiveTrue(Pageable pageable);

    // Lock the lobby record to prevent race conditions, so that the lobby can be updated if required, e.g. adding a new player
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Changed to JPQL query instead of native query due to InvalidDataAccessApiUsageException caused by setting lock on native query
    @Query("SELECT l FROM LobbyEntity l WHERE l.id = :id")
    Optional<LobbyEntity> findByIdForUpdate(@Param("id") Long id);

}
