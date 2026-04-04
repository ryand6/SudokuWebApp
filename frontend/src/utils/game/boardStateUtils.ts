import type { PrivateBoardState, PrivateCellState } from "@/types/game/GameTypes";
import { clearNotes } from "./noteUtils";

export function updateCellStateInBoardState(boardState: PrivateBoardState, rowIndex: number, colIndex: number, value: number, isRejected: boolean) {
    return boardState.map((row, r) => 
        r === rowIndex ? row.map((cell, c) => 
            c === colIndex
            ? {...cell, value: String(value), isRejected: isRejected, notes: clearNotes()} // optimistically set isRejected to false - will be overwritten if the server rejects
            : cell)
        : row);
}

export function updateNotesInBoardState(boardState: PrivateBoardState, rowIndex: number, colIndex: number, notes: number) {
    return boardState.map((row, r) => 
        r === rowIndex ? row.map((cell, c) => 
            c === colIndex
            ? {...cell, notes: notes}
            : cell)
        : row);
}

export function getCellState(boardState: PrivateBoardState, row: number, col: number): PrivateCellState {
    return boardState[row][col];
}