package com.github.ryand6.sudokuweb.exceptions.lobby;

public class UserExistsInActiveLobbyException extends RuntimeException {
    public UserExistsInActiveLobbyException(String message) {
        super(message);
    }
}
