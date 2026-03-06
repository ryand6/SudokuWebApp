package com.github.ryand6.sudokuweb.exceptions.game.state;

public class GamePlayerStateNotFoundException extends RuntimeException {
    public GamePlayerStateNotFoundException(String message) {
        super(message);
    }
}
