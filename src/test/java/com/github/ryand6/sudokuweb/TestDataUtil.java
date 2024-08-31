package com.github.ryand6.sudokuweb;

import com.github.ryand6.sudokuweb.domain.Score;
import com.github.ryand6.sudokuweb.domain.User;

public final class TestDataUtil {

    private TestDataUtil() {

    }

    public static User createTestUser() {
        return User.builder().
                id(1L).
                username("Henry").
                passwordHash("a4ceE42GHa").
                build();
    }

    public static Score createTestScore() {
        return Score.builder().
                id(1L).
                userId(1L).
                totalScore(150).
                gamesPlayed(1).
                build();
    }

}
