package com.github.ryand6.sudokuweb.enums;

public enum Difficulty {

    EASY, MEDIUM, HARD, EXTREME;

    // Return ENUM value in proper case
    public String getProperCase() {
        String lowerCase = name().toLowerCase();
        return Character.toUpperCase(lowerCase.charAt(0)) + lowerCase.substring(1);
    }

}
