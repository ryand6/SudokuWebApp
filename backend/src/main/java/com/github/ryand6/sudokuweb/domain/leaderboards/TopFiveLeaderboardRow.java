package com.github.ryand6.sudokuweb.domain.leaderboards;


public interface TopFiveLeaderboardRow {

    int getRank();

    Long getUserId();

    String getUsername();

    Long getTotalScore();

}
