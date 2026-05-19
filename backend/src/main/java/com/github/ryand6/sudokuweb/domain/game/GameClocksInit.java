package com.github.ryand6.sudokuweb.domain.game;

import com.github.ryand6.sudokuweb.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GameClocksInit {

    private Instant gameStartsAt;

    private Instant gameEndsAt;

    private GameStatus gameStatus;

}
