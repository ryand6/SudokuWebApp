package com.github.ryand6.sudokuweb.exceptions;

public class GamePlayerNotFoundException extends RuntimeException {
    public GamePlayerNotFoundException(String message) {
        super(message);
    }
}
