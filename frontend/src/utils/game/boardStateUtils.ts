import type { BoardState } from "@/types/game/GameTypes";

export function updateCellStateInBoardState(boardState: BoardState, rowIndex: number, colIndex: number, value: number) {
    return boardState.map((row, r) => 
        r === rowIndex ? row.map((cell, c) => 
            c === colIndex
            ? {...cell, value: String(value), isRejected: false} // optimistically set isRejected to false - will be overwritten if the server rejects
            : cell)
        : row);
}