package com.github.ryand6.sudokuweb.exceptions.game.player;

public class PlayerNotFinishedGameException extends RuntimeException {
    public PlayerNotFinishedGameException(String message) {
        super(message);
    }
}
