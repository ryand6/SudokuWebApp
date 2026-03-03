package com.github.ryand6.sudokuweb.exceptions.lobby;

public class ExistingActiveGameException extends RuntimeException {
    public ExistingActiveGameException(String message) {
        super(message);
    }
}
