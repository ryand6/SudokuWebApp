package com.github.ryand6.sudokuweb.exceptions;

public class UserExistsInActiveLobbyException extends RuntimeException {
    public UserExistsInActiveLobbyException(String message) {
        super(message);
    }
}
