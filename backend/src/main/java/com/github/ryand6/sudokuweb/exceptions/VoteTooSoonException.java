package com.github.ryand6.sudokuweb.exceptions;

public class VoteTooSoonException extends RuntimeException {
    private final Long secondsRemaining;

    public VoteTooSoonException(String message, Long secondsRemaining) {
        super(message);
        this.secondsRemaining = secondsRemaining;
    }

    public Long getSecondsRemaining() {
        return secondsRemaining;
    }
}
