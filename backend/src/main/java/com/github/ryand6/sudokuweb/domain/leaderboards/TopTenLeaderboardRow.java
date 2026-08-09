package com.github.ryand6.sudokuweb.domain.leaderboards;


public interface TopTenLeaderboardRow {

    int getRank();

    Long getUserId();

    String getUsername();

    Long getTotalScore();

}
