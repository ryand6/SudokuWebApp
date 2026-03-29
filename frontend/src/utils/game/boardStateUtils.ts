import type { PrivateBoardState } from "@/types/game/GameTypes";

export function updateCellStateInBoardState(boardState: PrivateBoardState, rowIndex: number, colIndex: number, value: number, isRejected: boolean) {
    return boardState.map((row, r) => 
        r === rowIndex ? row.map((cell, c) => 
            c === colIndex
            ? {...cell, value: String(value), isRejected: isRejected} // optimistically set isRejected to false - will be overwritten if the server rejects
            : cell)
        : row);
}