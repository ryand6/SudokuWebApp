package com.github.ryand6.sudokuweb.exceptions.game;

public class InvalidCellCoordinatesException extends RuntimeException {
    public InvalidCellCoordinatesException(String message) {
        super(message);
    }
}
