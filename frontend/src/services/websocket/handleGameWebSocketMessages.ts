import { gameChatCacheDispatcher } from "@/state/game/chat/gameChatCacheDispatcher";
import { gameEventsCacheDispatcher } from "@/state/game/events/gameEventsCacheDispatcher";
import { gameCacheDispatcher } from "@/state/game/gameCacheDispatcher";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { CellCoordinates } from "@/types/game/GameTypes";
import { updateGameHighlightedCells } from "@/utils/game/cellUtils";
import type { QueryClient } from "@tanstack/react-query";
import type { Dispatch, SetStateAction } from "react";
import type { NavigateFunction } from "react-router-dom";

export function handleGameWebSocketMessages(
    message: any, 
    queryClient: QueryClient,
    gameId: number,
    userId: number,
    playerColours: Record<number, PlayerColour>,
    navigate: NavigateFunction,
    setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>
) {
    switch (message.type) {
        case "PLAYER_CELL_UPDATE_ACCEPTED": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "PLAYER_CELL_UPDATE_ACCEPTED",
                userId: message.payload.userId,
                score: message.payload.score,
                firsts: message.payload.firsts,
                maxStreak: message.payload.maxStreak,
                gameEndsAt: message.payload.gameEndsAt,
                row: message.payload.row,
                col: message.payload.col,
                firstUserId: message.payload.firstUserId
            })
            break;
        }
        case "PLAYER_CELL_UPDATE_REJECTED": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "PLAYER_CELL_UPDATE_REJECTED",
                userId: message.payload.userId,
                score: message.payload.score,
                mistakes: message.payload.mistakes,
                gameEndsAt: message.payload.gameEndsAt
            })
            break;
        }
        case "GAME_EVENT": {
            gameEventsCacheDispatcher(queryClient, gameId, {
                type: "GAME_EVENT",
                newMessage: message.payload
            })
            break;
        }
        case "GAME_CHAT_MESSAGE": {
            gameChatCacheDispatcher(queryClient, gameId, userId, playerColours, {
                type: "GAME_CHAT_MESSAGE",
                newMessage: message.payload
            })
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