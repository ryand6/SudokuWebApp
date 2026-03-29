import type { GameMode } from "@/types/enum/GameMode";
import type { SharedBoardState, PrivateBoardState } from "@/types/game/GameTypes";

export function isBoardStateShared(gameMode: GameMode): boolean {
    return gameMode == "DOMINATION" || gameMode == "TIMEATTACK";
}

// creates the user's personal board state data depending on if the board is shared or private
export function resolveBoardState(
    gameMode: GameMode | undefined,
    sharedBoardState: SharedBoardState | undefined,
    privateBoardState: PrivateBoardState | undefined
): PrivateBoardState | undefined {
    if (!gameMode || !privateBoardState) return undefined;
    if (isBoardStateShared(gameMode) && sharedBoardState) {
        return sharedBoardState.map((row, r) => 
            row.map((cellState, c) => {
                const privateCellState = privateBoardState[r][c];
                // merges private and shared cell state data
                return {
                    value: cellState.value ?? (privateCellState.isRejected ? privateCellState.value : undefined),
                    notes: privateCellState.notes,
                    isRejected: privateCellState.isRejected
                }
            }))
    } else {
        return privateBoardState;
    }
}