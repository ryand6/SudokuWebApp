package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.LobbyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface LobbyRepository extends JpaRepository<LobbyEntity, Long> {

    // Checks if private lobby join code already exists elsewhere
    boolean existsByJoinCode(String joinCode);

    // Returns page containing list of active public lobbies within the pageable size range
    Page<LobbyEntity> findByIsPublicTrueAndIsActiveTrue(Pageable pageable);

}
