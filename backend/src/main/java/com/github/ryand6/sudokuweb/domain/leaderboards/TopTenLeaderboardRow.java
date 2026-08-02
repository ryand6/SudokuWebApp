package com.github.ryand6.sudokuweb.domain.leaderboards;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TopTenLeaderboardRow {

    private int rank;

    private Long userId;

    private String username;

    private long total_score;

}
