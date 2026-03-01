package com.github.ryand6.sudokuweb.exceptions.game;

public class GameCreationInterruptedException extends RuntimeException {
    public GameCreationInterruptedException(String message) {
        super(message);
    }
}
