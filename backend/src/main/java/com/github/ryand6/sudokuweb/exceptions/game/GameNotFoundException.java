package com.github.ryand6.sudokuweb.exceptions.game;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
