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
public class CellAcceptedPublicUpdate {

    private Long userId;

    private Integer score;

    private Integer firsts;

    private Integer maxStreak;

    private Instant gameEndsAt;

    private int row;

    private int col;

    private Long firstUserId;

}
