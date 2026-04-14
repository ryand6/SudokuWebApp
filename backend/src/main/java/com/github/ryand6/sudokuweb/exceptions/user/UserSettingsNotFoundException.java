package com.github.ryand6.sudokuweb.exceptions.user;

public class UserSettingsNotFoundException extends RuntimeException {
    public UserSettingsNotFoundException(String message) {
        super(message);
    }
}
