package com.github.ryand6.sudokuweb.dto.entity.game;

import com.github.ryand6.sudokuweb.enums.GameEventType;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameEventDto {

    private Long gameId;

    private Long userId;

    private Instant createdAt;

    private GameEventType eventType;

    private String message;

    private Long sequenceNumber;

}
