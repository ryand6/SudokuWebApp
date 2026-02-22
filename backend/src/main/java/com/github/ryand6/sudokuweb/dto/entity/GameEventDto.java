package com.github.ryand6.sudokuweb.dto.entity;

import com.github.ryand6.sudokuweb.enums.GameEventType;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameEventDto {

    private Long gameId;

    private Long userId;

    private GameEventType eventType;

    private Map<String, Object> payload;

    private Long sequenceNumber;

}
