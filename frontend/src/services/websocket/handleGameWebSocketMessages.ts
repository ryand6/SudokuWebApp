import { gameCacheDispatcher } from "@/state/game/gameCacheDispatcher";
import type { CellCoordinates } from "@/types/game/GameTypes";
import { updateGameHighlightedCells } from "@/utils/game/cellUtils";
import type { QueryClient } from "@tanstack/react-query";
import type { Dispatch, SetStateAction } from "react";
import type { NavigateFunction } from "react-router-dom";

export function handleGameWebSocketMessages(
    message: any, 
    queryClient: QueryClient,
    gameId: number, 
    navigate: NavigateFunction,
    setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>
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
            setGameHighlightedCells(prev => updateGameHighlightedCells(message.payload.userId, prev, { row: message.payload.row, col: message.payload.col }))
            break;
        }
    }
}