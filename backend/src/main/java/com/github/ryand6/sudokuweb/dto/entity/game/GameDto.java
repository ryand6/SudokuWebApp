package com.github.ryand6.sudokuweb.dto.entity.game;

import com.github.ryand6.sudokuweb.enums.Difficulty;
import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameStatus;
import com.github.ryand6.sudokuweb.enums.TimeLimitPreset;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameDto {

    private Long id;

    private Long lobbyId;

    private Set<GamePlayerDto> gamePlayers;

    private String initialBoardState;

    private SharedGameStateDto sharedGameState;

    private GameMode gameMode;

    private Difficulty difficulty;

    private TimeLimitPreset timeLimit;

    private GameStatus gameStatus;

    private Instant gameStartsAt;

    private Instant gameEndsAt;

}
