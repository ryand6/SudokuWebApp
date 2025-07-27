package com.github.ryand6.sudokuweb.exceptions;

public class TooManyActivePlayersException extends RuntimeException {

    public TooManyActivePlayersException(String message) {
        super(message);
    }

}
