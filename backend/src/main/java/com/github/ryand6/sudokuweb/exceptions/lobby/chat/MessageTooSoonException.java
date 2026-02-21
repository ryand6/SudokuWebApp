package com.github.ryand6.sudokuweb.exceptions.lobby.chat;

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
