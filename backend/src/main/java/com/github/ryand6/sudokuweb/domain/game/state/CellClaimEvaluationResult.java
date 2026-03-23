package com.github.ryand6.sudokuweb.domain.game.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CellClaimEvaluationResult {

    private int cellClaimPosition;

    private Long previousFirstWinner;

}
