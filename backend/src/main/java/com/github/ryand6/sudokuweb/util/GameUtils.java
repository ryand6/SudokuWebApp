package com.github.ryand6.sudokuweb.util;

import com.github.ryand6.sudokuweb.enums.CellStatus;
import com.github.ryand6.sudokuweb.exceptions.game.InvalidCellCoordinatesException;

public final class GameUtils {

    public static void validateCellCoordinates(int row, int col) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            throw new InvalidCellCoordinatesException("Cell co-ordinates (" + row + "," + col + ") are invalid");
        }
    }

    public static CellStatus[] convertBoardStateIntoProgressState(String boardState, String initialBoardState) {
        int boardStateLength = boardState.length();
        CellStatus[] progressState = new CellStatus[boardStateLength];
        for (int i = 0; i < boardStateLength; i++) {
            progressState[i] = boardState.charAt(i) == '.' ? CellStatus.INCOMPLETE : determineGiven(i, initialBoardState);
        }
        return progressState;
    }

    private static CellStatus determineGiven(int index, String initialBoardState) {
        return initialBoardState.charAt(index) == '.' ? CellStatus.WON : CellStatus.GIVEN;
    };

}
