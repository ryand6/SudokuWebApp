import type { PrivateCellState, PrivateGamePlayerState } from "@/types/game/GameTypes";
import type { GamePlayerStateEvent } from "../gameEvents";
import { clearCellState, updateCellStateInBoardState, updateNotesInBoardState } from "@/utils/game/boardStateUtils";
import { notificationEmitter } from "@/utils/game/gameNotificationUtils";
import { toggleNote } from "@/utils/game/noteUtils";

export class CellUpdateValidationError extends Error {}
export class NotesUpdateValidationError extends Error {}

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
            const newBoard = updateCellStateInBoardState(existingData.boardState, event.row, event.col, event.value, false, true);
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
            const newBoard = updateCellStateInBoardState(existingData.boardState, event.row, event.col, event.value, false, false);
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
            const newBoard = updateCellStateInBoardState(existingData.boardState, event.row, event.col, event.value, true, false);
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
        case "STREAK_RESET": {
            notificationEmitter.emit({ type: "streak", message: "Streak Lost" });
            return {
                ...existingData,
                currentStreak: 0
            }
        }
        // Handles optimistic UI update prior to server validation
        case "NOTE_UPDATE": {
            if (event.note < 0 || event.note > 9) {
                throw new NotesUpdateValidationError("Submitted note outside of valid range");
            }
            const boardCell = existingData.boardState[event.row][event.col];
            if (boardCell.value && !boardCell.isRejected) {
                throw new NotesUpdateValidationError("Valid answer already exists for cell");
            }
            const updatedCellNotes = toggleNote(boardCell.notes, event.note);
            if (updatedCellNotes < 0 || updatedCellNotes > 511) {
                throw new NotesUpdateValidationError("Submitted notes outside of valid range");
            }
            const newBoard = updateNotesInBoardState(existingData.boardState, event.row, event.col, updatedCellNotes);
            return {
                ...existingData,
                boardState: newBoard
            };
        }
        case "CELL_CLEAR": {
            const newBoard = clearCellState(existingData.boardState, event.row, event.col);
            return {
                ...existingData,
                boardState: newBoard
            }
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