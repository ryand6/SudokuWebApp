package com.github.ryand6.sudokuweb.enums;

import com.github.ryand6.sudokuweb.util.StringUtils;

public enum Difficulty {

    EASY, MEDIUM, HARD, EXTREME;

    // Return ENUM value in proper case
    public String getProperCase() {
        return StringUtils.getProperCase(name());
    }

}
