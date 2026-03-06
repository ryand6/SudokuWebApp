package com.github.ryand6.sudokuweb.exceptions.game.state;

public class IllegalBoardStateException extends RuntimeException {
    public IllegalBoardStateException (String message) {
        super(message);
    }
}
