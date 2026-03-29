import type { PrivateCellState, PrivateGamePlayerState } from "@/types/game/GameTypes";
import type { GamePlayerStateEvent } from "../gameEvents";
import { updateCellStateInBoardState } from "@/utils/game/boardStateUtils";
import { notificationEmitter } from "@/utils/game/gameNotificationUtils";

export class CellUpdateValidationError extends Error {}

export function gamePlayerStateCacheReducer(
    existingData: PrivateGamePlayerState,
    event: GamePlayerStateEvent
): PrivateGamePlayerState {
    switch (event.type) {
        // Handles optimistic UI update prior to server validation
        case "CELL_UPDATE_SUBMITTED": {
            const cacheState: PrivateCellState = existingData.boardState[event.row][event.col];
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
            const newBoard = updateCellStateInBoardState(existingData.boardState, event.row, event.col, event.value, false);
            return {
                ...existingData,
                boardState: newBoard
            };
        }
        case "CELL_UPDATE_ACCEPTED": {
            const prevStreak: number = existingData.currentStreak;
            if (prevStreak !== event.currentStreak) {
                emitStreakUpdate(prevStreak, event.currentStreak);
            }
            const newBoard = updateCellStateInBoardState(existingData.boardState, event.row, event.col, event.value, false);
            return {
                ...existingData,
                currentStreak: event.currentStreak,
                boardState: newBoard
            };
        }
        case "CELL_UPDATE_REJECTED": {
            const prevStreak: number = existingData.currentStreak;
            if (prevStreak !== event.currentStreak) {
                emitStreakUpdate(prevStreak, event.currentStreak);
            }
            const newBoard = updateCellStateInBoardState(existingData.boardState, event.row, event.col, event.value, true);
            return {
                ...existingData,
                currentStreak: event.currentStreak,
                boardState: newBoard
            };
        }
        case "CELL_UPDATE_INVALID": {
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

function emitStreakUpdate(prevStreak: number, currentStreak: number) {
    if (currentStreak === 1 || (prevStreak === 1 && currentStreak === 0)) return;

    let message = "";

    if (currentStreak === 0) {
        message = "Streak Lost";
    } else {
        message = `x${currentStreak} Streak!`;
    }

    notificationEmitter.emit({ type: "streak", message: message });

}