package com.github.ryand6.sudokuweb.util;

import com.github.ryand6.sudokuweb.exceptions.game.InvalidCellCoordinatesException;

public class GameUtils {

    public static void validateCellCoordinates(int row, int col) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            throw new InvalidCellCoordinatesException("Cell co-ordinates (" + row + "," + col + ") are invalid");
        }
    }

}
