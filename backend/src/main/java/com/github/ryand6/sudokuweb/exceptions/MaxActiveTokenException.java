package com.github.ryand6.sudokuweb.exceptions;

public class MaxActiveTokenException extends RuntimeException {
    public MaxActiveTokenException(String message) {
        super(message);
    }
}
