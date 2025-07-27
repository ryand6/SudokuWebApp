package com.github.ryand6.sudokuweb.enums;

import lombok.Getter;

@Getter
public enum PreferenceDirection {

    LESS("Less", "Shorter", "Easier"),
    EQUAL("Same", "Same", "Same"),
    MORE("More", "Longer", "Harder");

    private final String genericLabel;
    private final String durationLabel;
    private final String difficultyLabel;

    PreferenceDirection(String genericLabel, String durationLabel, String difficultyLabel) {
        this.genericLabel = genericLabel;
        this.durationLabel = durationLabel;
        this.difficultyLabel = difficultyLabel;
    }

}
