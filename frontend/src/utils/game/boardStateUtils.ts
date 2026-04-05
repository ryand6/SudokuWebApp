import type { PrivateBoardState, PrivateCellState } from "@/types/game/GameTypes";
import { clearNote, clearNotes } from "./noteUtils";

export function updateCellStateInBoardState(
    boardState: PrivateBoardState, 
    rowIndex: number, 
    colIndex: number, 
    value: number, 
    isRejected: boolean, 
    isOptimisticUpdate: boolean
): PrivateBoardState {
    return boardState.map((row, r) => 
        row.map((cell, c) => {
            if (r === rowIndex && c === colIndex) {
                return {
                    ...cell,
                    value: String(value),
                    isRejected: isRejected,
                    notes: clearNotes()
                }
            }
            if (isRejected || isOptimisticUpdate) return cell;
            const inSameRow = rowIndex === r;
            const inSameCol = colIndex === c;
            const inSameBlock = (Math.floor(r / 3) === Math.floor(rowIndex / 3)) && (Math.floor(c / 3) === Math.floor(colIndex / 3));
            if (!inSameRow && !inSameCol && !inSameBlock) return cell;
            return {
                ...cell,
                notes: clearNote(cell.notes, value)
            }
        })
    );
}

export function clearCellState(
    boardState: PrivateBoardState, 
    rowIndex: number, 
    colIndex: number
): PrivateBoardState {
    return boardState.map((row, r) =>
        r === rowIndex ? row.map((cell, c) => 
            c === colIndex 
            ? clearCell(cell)
            : cell)
        : row
    );
}

function clearCell(cell: PrivateCellState): PrivateCellState {
    return {
        ...cell,
        value: cell.value && !cell.isRejected ? cell.value : undefined,
        isRejected: false,
        notes: clearNotes()
    }
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