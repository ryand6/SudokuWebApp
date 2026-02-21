package com.github.ryand6.sudokuweb.exceptions.lobby;

public class LobbyFullException extends RuntimeException {
    public LobbyFullException(String message) {
        super(message);
    }
}
