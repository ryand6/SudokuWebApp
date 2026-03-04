package com.github.ryand6.sudokuweb.exceptions.game;

public class IllegalGameStatusChangeException extends RuntimeException {
    public IllegalGameStatusChangeException(String message) {
        super(message);
    }
}
