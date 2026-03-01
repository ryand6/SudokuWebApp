package com.github.ryand6.sudokuweb.domain.lobby;

import com.github.ryand6.sudokuweb.domain.lobby.LobbyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LobbyRepository extends JpaRepository<LobbyEntity, Long> {

    // Returns page containing list of active public lobbies within the pageable size range
    Page<LobbyEntity> findByIsActiveTrueAndLobbySettingsEntity_IsPublicTrue(Pageable pageable);

    // Finds active lobbies where the user is a player in - business logic uses this to enforce a constraint where the user can
    // only be a player in one active lobby at a time
    Set<LobbyEntity> findByLobbyPlayers_User_IdAndIsActiveTrue(Long userId);

    // Returns a list of lobby entities where the countdowns have now ended
    @Query(
            value = """
            SELECT lobby
            FROM LobbyEntity lobby
            WHERE lobby.lobbyCountdownEntity.countdownActive = true
            AND lobby.lobbyCountdownEntity.countdownEndsAt <= :now
            AND lobby.inGame = false
        """)
    List<LobbyEntity> findAllLobbiesWithExpiredCountdowns(@Param("now") Instant now);

}
