import type { PublicGameState } from "@/types/game/GameTypes";
import type { GameEvent } from "./gameEvents";

export function gameCacheReducer(
    oldData: PublicGameState,
    event: GameEvent
): PublicGameState {
    switch (event.type) {
        case "GAME_PLAYER_UPDATE": {
            // IMPLEMENT
            return oldData;
        }
        case "BOARD_PROGRESS_UPDATE": {
            // IMPLEMENT FUNCTION FOR HANDLING INCREMENTAL BOARD PROGRESS UPDATES PER PLAYER

            // DETERMINE TO KEEP THIS SEPARATE, OR JUST DO GAME PLAYER UPDATE
            return oldData;
        }
        case "GAME_STATUS_UPDATE": {
            // IMPLEMENT
            return oldData;
        }
        case "GAME_TIMER_UPDATE": {
            // IMPLEMENT - gameStartsAt and gameEndsAt ?
            return oldData;
        }
        case "HIGHLIGHTED_CELL_UPDATE": {
            const player = oldData.players[event.playerId];
            if (!player) return oldData;
            return {
                ...oldData,
                players: {
                    ...oldData.players,
                    [event.playerId]: {
                        ...oldData.players[event.playerId],
                        currentHighlightedCell: event.coordinates ?? null
                    }
                }
            }
        }
        default: 
            return oldData;
    }
}