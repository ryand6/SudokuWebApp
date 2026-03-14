import type { PublicGameState } from "@/types/game/GameTypes";
import type { GameEvent } from "./gameEvents";

export function gameCacheReducer(
    existingData: PublicGameState,
    event: GameEvent
): PublicGameState {
    switch (event.type) {
        case "GAME_PLAYER_UPDATE": {
            // IMPLEMENT
            return existingData;
        }
        case "BOARD_PROGRESS_UPDATE": {
            // IMPLEMENT FUNCTION FOR HANDLING INCREMENTAL BOARD PROGRESS UPDATES PER PLAYER

            // DETERMINE TO KEEP THIS SEPARATE, OR JUST DO GAME PLAYER UPDATE
            return existingData;
        }
        case "GAME_STATUS_UPDATE": {
            // IMPLEMENT
            return existingData;
        }
        case "GAME_TIMER_UPDATE": {
            // IMPLEMENT - gameStartsAt and gameEndsAt ?
            return existingData;
        }
        case "HIGHLIGHTED_CELL_UPDATE": {
            const player = existingData.players[event.playerId];
            if (!player) return existingData;
            return {
                ...existingData,
                players: {
                    ...existingData.players,
                    [event.playerId]: {
                        ...existingData.players[event.playerId],
                        currentHighlightedCell: event.coordinates ?? null
                    }
                }
            }
        }
        default: 
            return existingData;
    }
}