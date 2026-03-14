import { gameCacheDispatcher } from "@/state/game/gameCacheDispatcher";
import { queryKeys } from "@/state/queryKeys";
import type { PlayerHighlightedCellResponseDto } from "@/types/dto/response/PlayerHighlightedCellResponseDto";
import type { PublicGameState } from "@/types/game/GameTypes";
import type { QueryClient } from "@tanstack/react-query";
import type { NavigateFunction } from "react-router-dom";

export function handleGameWebSocketMessages(message: any, queryClient: QueryClient, gameId: number, navigate: NavigateFunction) {
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
            gameCacheDispatcher(queryClient, gameId, {
                type: message.type,
                playerId: message.payload.playerId,
                coordinates: message.payload.coordinates
            });
            break;
        }
        
    }
}