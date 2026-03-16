import { gameCacheDispatcher } from "@/state/game/gameCacheDispatcher";
import type { QueryClient } from "@tanstack/react-query";
import type { NavigateFunction } from "react-router-dom";

export function handleGameWebSocketMessages(
    message: any, 
    queryClient: QueryClient, 
    gameId: number, 
    navigate: NavigateFunction
) {
    switch (message.type) {
        case "GAME_PLAYER_UPDATE": {
            // IMPLEMENT
            break;
        }
        case "BOARD_PROGRESS_UPDATE": {
            // IMPLEMENT FUNCTION FOR HANDLING INCREMENTAL BOARD PROGRESS UPDATES PER PLAYER

            // DETERMINE TO KEEP THIS SEPARATE, OR JUST DO GAME PLAYER UPDATE
            break;
        }
        case "GAME_STATUS_UPDATE": {
            // IMPLEMENT
            break;
        }
        case "GAME_TIMER_UPDATE": {
            // IMPLEMENT - gameStartsAt and gameEndsAt ?
            break;
        }
        case "HIGHLIGHTED_CELL_UPDATE": {
            // Remove - don't use game dispatcher, instead update using setState
            gameCacheDispatcher(queryClient, gameId, {
                type: message.type,
                userId: message.payload.userId,
                coordinates: {
                    row: message.payload.row,
                    col: message.payload.col
                }
            });
            break;
        }
        
    }
}