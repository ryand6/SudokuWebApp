package com.github.ryand6.sudokuweb.exceptions.lobby;

public class LobbyNotFoundException extends RuntimeException {
    public LobbyNotFoundException(String message) {
        super(message);
    }
}
