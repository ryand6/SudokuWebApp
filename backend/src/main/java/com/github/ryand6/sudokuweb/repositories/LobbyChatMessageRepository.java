package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.LobbyChatMessageEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

public interface LobbyChatMessageRepository extends JpaRepository<LobbyChatMessageEntity, Long> {

    // Returns page containing list of messages for
    Page<LobbyChatMessageEntity> findByLobbyEntity_IdOrderByCreatedAtDesc(Long lobbyId, Pageable pageable);

    // Used to trim down lobby messages so that only the most recent 100 messages are persisted
    // Using middle sub-query to ensure the ids are read into memory before attempting a deletion on the table
    // NOT CURRENTLY IN USE
    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM lobby_chat_messages
            WHERE lobby_id = :lobbyId
            AND id NOT IN (
                SELECT id FROM (
                    SELECT id FROM lobby_chat_messages
                    WHERE lobby_id = :lobbyId
                    ORDER BY created_at DESC
                    LIMIT 100
                ) AS temp
            )""", nativeQuery = true)
    void trimOldMessages(@Param("lobbyId") Long lobbyId);

}
