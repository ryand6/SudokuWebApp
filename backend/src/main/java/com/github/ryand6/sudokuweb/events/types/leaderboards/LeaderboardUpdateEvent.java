package com.github.ryand6.sudokuweb.events.types.leaderboards;

import com.github.ryand6.sudokuweb.enums.GameMode;
import com.github.ryand6.sudokuweb.enums.GameResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LeaderboardUpdateEvent {

    private GameResult gameResult;

    private GameMode gameMode;

    private Long userId;

    private Integer leaderboardScore;

}
