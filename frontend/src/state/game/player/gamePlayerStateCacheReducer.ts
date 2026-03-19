import type { CellState, PrivateGamePlayerState } from "@/types/game/GameTypes";
import type { GamePlayerStateEvent } from "../gameEvents";
import { updateCellStateInBoardState } from "@/utils/game/boardStateUtils";

export class CellUpdateValidationError extends Error {}

export function gamePlayerStateCacheReducer(
    existingData: PrivateGamePlayerState,
    event: GamePlayerStateEvent
): PrivateGamePlayerState {
    switch (event.type) {
        // Handles optimistic UI update prior to server validation
        case "CELL_UPDATE_SUBMITTED": {
            const cacheState: CellState = existingData.boardState[event.row][event.col];
            if (cacheState.value === String(event.value)) {
                throw new CellUpdateValidationError("Inputted number already exists in cache");
            }
            // Invalid answers in the cache can be overwritten - these are not synced in the backend, the backend only stores correct answers
            if (cacheState.value && !cacheState.isRejected) {
                throw new CellUpdateValidationError("Cell already contains a valid answer");
            }
            if (event.value < 1 || event.value > 9) {
                throw new CellUpdateValidationError("Submitted value out of valid range 1 - 9");
            }
            const newBoard = updateCellStateInBoardState(existingData.boardState, event.row, event.col, event.value);
            return {
                ...existingData,
                boardState: newBoard
            };
        }
        case "CELL_UPDATE_ACCEPTED": {
            // IMPLEMENT
            return existingData;
        }
        case "CELL_UPDATE_REJECTED": {
            // IMPLEMENT
            return existingData;
        }
        // Handles optimistic UI update prior to server validation
        case "NOTE_UPDATE": {
            // IMPLEMENT 
            return existingData;
        }
        case "SETTINGS_UPDATED": {
            // IMPLEMENT
            return existingData;
        }
        default: 
            return existingData;
    }
}