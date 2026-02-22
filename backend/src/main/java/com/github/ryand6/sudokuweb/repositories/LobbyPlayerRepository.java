package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.LobbyPlayerEntity;
import com.github.ryand6.sudokuweb.domain.LobbyPlayerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LobbyPlayerRepository extends JpaRepository<LobbyPlayerEntity, LobbyPlayerId> {

    @Query(value = "SELECT * FROM lobby_players WHERE lobby_id = :lobbyId AND user_id = :userId", nativeQuery = true)
    Optional<LobbyPlayerEntity> findByCompositeId(@Param("lobbyId") Long lobbyId, @Param("userId") Long userId);

    // Check is a user is an active member in a lobby
    boolean existsByUser_IdAndLobby_Id(Long userId, Long lobbyId);

}
