package com.github.ryand6.sudokuweb.exceptions.game;

public class TooManyActivePlayersException extends RuntimeException {

    public TooManyActivePlayersException(String message) {
        super(message);
    }

}
