package com.github.ryand6.sudokuweb.enums;

import lombok.Getter;

@Getter
public enum TimeLimitPreset {

    // Game duration settings
    UNLIMITED("No Time Limit", null),
    QUICK("Quick Game", 900),
    STANDARD("Standard", 1800),
    MARATHON("Marathon", 3600);

    private final String displayName;
    private final Integer seconds;

    // Called when enum constant is created
    TimeLimitPreset(String displayName, Integer seconds) {
        this.displayName = displayName;
        this.seconds = seconds;
    }

    public boolean isUnlimited() {
        return seconds == null;
    }

    public String getFormattedTime() {
        if (isUnlimited()) {
            return "Unlimited";
        }
        // Represent time in minutes
        return String.format("%d minutes", seconds / 60);
    }

}
