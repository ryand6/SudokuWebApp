package com.github.ryand6.sudokuweb.exceptions.game.state;

public class InvalidSharedGameStateException extends RuntimeException {
    public InvalidSharedGameStateException(String message) {
        super(message);
    }
}
