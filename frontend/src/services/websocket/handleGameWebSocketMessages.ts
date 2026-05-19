import { gameChatCacheDispatcher } from "@/state/game/chat/gameChatCacheDispatcher";
import { gameEventsCacheDispatcher } from "@/state/game/events/gameEventsCacheDispatcher";
import { gameCacheDispatcher } from "@/state/game/gameCacheDispatcher";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { CellCoordinates } from "@/types/game/GameTypes";
import { updateGameHighlightedCells } from "@/utils/game/cellUtils";
import type { QueryClient } from "@tanstack/react-query";
import type { Dispatch, SetStateAction } from "react";
import type { NavigateFunction } from "react-router-dom";
import { toast } from "react-toastify";

export function handleGameWebSocketMessages(
    message: any, 
    queryClient: QueryClient,
    gameId: number,
    userId: number,
    gameNotificationsEnabled: boolean,
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
            gameChatCacheDispatcher(queryClient, gameId, userId, gameNotificationsEnabled, playerColours, {
                type: "GAME_CHAT_MESSAGE",
                newMessage: message.payload
            })
            break;
        }
        case "HIGHLIGHTED_CELL_UPDATE": {
            setGameHighlightedCells(prev => updateGameHighlightedCells(message.payload.userId, prev, { row: message.payload.row, col: message.payload.col }))
            break;
        }
        case "PLAYER_FORFEIT": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "PLAYER_FORFEIT",
                gamePlayer: message.payload
            })
            if (message.payload.user.id !== userId) {
                toast.info(`${message.payload.user.username} has forfeited the game`, { containerId: "foreground" });
            }
            break;
        }
        case "PLAYER_FINISHED": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "PLAYER_FINISHED",
                gamePlayer: message.payload
            })
            break;
        }
        case "GAME_ENDED_PREMATURELY": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "GAME_ENDED_PREMATURELY"
            })
            break;
        }
        case "GAME_RESULTS_DETERMINED": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "GAME_RESULTS_DETERMINED",
                gameResults: message.payload
            })
            break;
        }
        case "GAME_STATUS_UPDATE": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "GAME_STATUS_UPDATE",
                gameStatus: message.payload
            })
            break;
        }
        case "GAME_END_TIMER_UPDATE": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "GAME_END_TIMER_UPDATE",
                gameEndsAt: message.payload
            })
            break;
        }
        case "GAME_CLOCKS_INITIALISED": {
            gameCacheDispatcher(queryClient, gameId, {
                type: "GAME_CLOCKS_INITIALISED",
                gameStartsAt: message.payload.gameStartsAt,
                gameEndsAt: message.payload.gameEndsAt,
                gameStatus: message.payload.gameStatus
            })
            break;
        }
    }
}