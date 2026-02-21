package com.github.ryand6.sudokuweb.exceptions.user;

public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String message) {
        super(message);
    }
}
