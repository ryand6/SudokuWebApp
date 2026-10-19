import { lobbyChatCacheDispatcher } from "@/state/lobby/chat/lobbyChatCacheDispatcher";
import { gameCreationCacheDispatcher } from "@/state/lobby/gameCreationCacheDispatcher";
import { lobbyCacheDispatcher } from "@/state/lobby/lobbyCacheDispatcher";
import { QueryClient } from "@tanstack/react-query";
import { type NavigateFunction } from "react-router-dom";

export function handleLobbyWebSocketMessages(message: any, queryClient: QueryClient, lobbyId: number, navigate: NavigateFunction) {
    switch (message.type) {
        case "LOBBY_DIFFICULTY_UPDATED": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_DIFFICULTY_UPDATED",
                lobbyId: message.payload.lobbyId,
                difficulty: message.payload.difficulty
            })
            break;
        }
        case "LOBBY_TIME_LIMIT_UPDATED": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_TIME_LIMIT_UPDATED",
                lobbyId: message.payload.lobbyId,
                timeLimit: message.payload.timeLimit
            })
            break;
        }
        case "LOBBY_PLAYER_STATUS_UPDATED": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_PLAYER_STATUS_UPDATED",
                lobbyId: message.payload.lobbyId,
                userId: message.payload.userId,
                lobbyStatus: message.payload.lobbyStatus,
                readyAt: message.payload.readyAt,
                lobbyCountdown: message.payload.lobbyCountdown
            })
            break;
        }
        case "LOBBY_PLAYER_JOINED": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_PLAYER_JOINED",
                lobbyId: message.payload.lobbyId,
                lobbyPlayer: message.payload.lobbyPlayer
            })
            break;
        }
        case "LOBBY_PLAYER_LEFT": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_PLAYER_LEFT",
                lobbyId: message.payload.lobbyId,
                userId: message.payload.userId,
                isActive: message.payload.isActive,
                host: message.payload.host,
                lobbyCountdown: message.payload.lobbyCountdown
            })
            break;
        }
        case "LOBBY_GAME_CREATED": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_GAME_CREATED",
                lobbyId: message.payload.lobbyId,
                gameId: message.payload.gameId,
                inGame: message.payload.inGame
            })
            break;
        }
        // Transport lobby players to game page when game has started
        case "GAME_CREATED": {
            gameCreationCacheDispatcher(queryClient, navigate, {
                type: "GAME_CREATED",
                gameData: message.payload
            })
            break;
        }
        case "GAME_ENDED": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "GAME_ENDED",
                lobbyId: message.payload.lobbyId
            })
            break;
        }
        // Updates session storage if message is received in lobby chat
        case "LOBBY_CHAT_MESSAGE": {
            lobbyChatCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_CHAT_MESSAGE",
                newMessage: message.payload
            })
            break;
        }
    }
}