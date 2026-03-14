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
            const payload: PlayerHighlightedCellResponseDto = message.payload;
            queryClient.setQueryData(["game", gameId], (old: PublicGameState) => {
                if (!old) return old;
                const player = old.players[payload.playerId];
                if (!player) return old;
                return {
                    ...old,
                    players: {
                        ...old.players,
                        [payload.playerId]: {
                            ...old.players[payload.playerId],
                            currentHighlightedCell: payload.coordinates ?? null
                        }
                    }
                }
            })
            break;
        }
        
    }
}