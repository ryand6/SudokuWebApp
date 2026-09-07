package com.github.ryand6.sudokuweb.domain.leaderboards;

public interface LeaderboardRow {

    long getRank();

    Long getUserId();

    String getUsername();

    Long getTotalScore();

    int getGamesPlayed();

    int getWins();

    int getLosses();

    int getDraws();

    int getMaxWinStreak();

}
