package com.github.ryand6.sudokuweb.exceptions;

public class MessageTooSoonException extends RuntimeException {
    private final Long secondsRemaining;

    public MessageTooSoonException(String message, Long secondsRemaining) {
        super(message);
        this.secondsRemaining = secondsRemaining;
    }

    public Long getSecondsRemaining() {
        return secondsRemaining;
    }
}
