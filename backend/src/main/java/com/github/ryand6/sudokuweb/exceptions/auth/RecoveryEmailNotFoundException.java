package com.github.ryand6.sudokuweb.exceptions.auth;

public class RecoveryEmailNotFoundException extends RuntimeException {
    public RecoveryEmailNotFoundException(String message) {
        super(message);
    }
}
