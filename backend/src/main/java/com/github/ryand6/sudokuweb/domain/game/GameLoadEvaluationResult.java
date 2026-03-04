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
public class GameLoadEvaluationResult {

    private Instant gameStartsAt;

    private Instant gameEndsAt;

}
