package com.github.ryand6.sudokuweb.domain.game.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameChatMessageRepository extends JpaRepository<GameChatMessageEntity, Long> {

    Page<GameChatMessageEntity> findByGameEntity_IdOrderByCreatedAtDesc(Long gameId, Pageable pageable);

}
