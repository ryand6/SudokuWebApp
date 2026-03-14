import { lobbyChatCacheDispatcher } from "@/state/lobby/chat/lobbyChatCacheDispatcher";
import { gameCreationCacheDispatcher } from "@/state/lobby/gameCreationCacheDispatcher";
import { lobbyCacheDispatcher } from "@/state/lobby/lobbyCacheDispatcher";
import { QueryClient } from "@tanstack/react-query";
import { type NavigateFunction } from "react-router-dom";

export function handleLobbyWebSocketMessages(message: any, queryClient: QueryClient, lobbyId: number, navigate: NavigateFunction) {
    switch (message.type) {
        // Updates React Query lobby cache if the lobby is updated in the backend
        case "LOBBY_UPDATED": {
            lobbyCacheDispatcher(queryClient, lobbyId, {
                type: "LOBBY_UPDATED",
                lobbyData: message.payload
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