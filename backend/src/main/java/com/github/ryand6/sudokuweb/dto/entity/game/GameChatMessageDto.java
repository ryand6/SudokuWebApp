package com.github.ryand6.sudokuweb.dto.entity.game;

import com.github.ryand6.sudokuweb.enums.MessageType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameChatMessageDto {

    private Long id;

    private Long gameId;

    private Long userId;

    private String username;

    private String message;

    private MessageType messageType;

    private Instant createdAt;

}
