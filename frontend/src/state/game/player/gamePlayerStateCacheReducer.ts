import type { PrivateGamePlayerState } from "@/types/game/GameTypes";
import type { GamePlayerStateEvent } from "../gameEvents";
import { updateCellStateInBoardState } from "@/utils/game/boardStateUtils";

export function gamePlayerStateCacheReducer(
    existingData: PrivateGamePlayerState,
    event: GamePlayerStateEvent
): PrivateGamePlayerState {
    switch (event.type) {
        // Handles optimistic UI update prior to server validation
        case "CELL_UPDATE_SUBMITTED": {
            if (existingData.boardState[event.row][event.col] || event.value < 1 || event.value > 9) return existingData;
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