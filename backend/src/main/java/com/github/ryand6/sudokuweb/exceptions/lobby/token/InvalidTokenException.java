package com.github.ryand6.sudokuweb.exceptions.lobby.token;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
