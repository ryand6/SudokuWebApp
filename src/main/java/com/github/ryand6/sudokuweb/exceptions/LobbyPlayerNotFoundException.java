package com.github.ryand6.sudokuweb.exceptions;

public class LobbyPlayerNotFoundException extends RuntimeException {
    public LobbyPlayerNotFoundException(String message) {
        super(message);
    }
}
