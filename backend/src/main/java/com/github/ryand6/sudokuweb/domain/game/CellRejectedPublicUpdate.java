package com.github.ryand6.sudokuweb.domain.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CellRejectedPublicUpdate {

    private Long userId;

    private Integer score;

    private Integer mistakes;

    private Instant gameEndsAt;

}
