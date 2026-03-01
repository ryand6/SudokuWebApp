package com.github.ryand6.sudokuweb.exceptions.lobby;

public class LobbyOptimisticLockException extends RuntimeException {
    public LobbyOptimisticLockException(String message) {
        super(message);
    }
}
