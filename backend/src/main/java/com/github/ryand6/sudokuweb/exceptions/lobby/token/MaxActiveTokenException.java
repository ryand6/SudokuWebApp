package com.github.ryand6.sudokuweb.exceptions.lobby.token;

public class MaxActiveTokenException extends RuntimeException {
    public MaxActiveTokenException(String message) {
        super(message);
    }
}
