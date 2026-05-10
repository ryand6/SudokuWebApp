package com.github.ryand6.sudokuweb.events.types.game;

import com.github.ryand6.sudokuweb.domain.game.player.LeaderboardScoreCalculation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerLeaderboardScoreEvent {

    private Long gameId;

    private Long userId;

    private LeaderboardScoreCalculation leaderboardScoreCalculation;

}
