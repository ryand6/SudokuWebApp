package com.github.ryand6.sudokuweb.exceptions.lobby.player;

public class LobbyPlayerAlreadyExistsException extends RuntimeException {
    public LobbyPlayerAlreadyExistsException(String message) {
        super(message);
    }
}
