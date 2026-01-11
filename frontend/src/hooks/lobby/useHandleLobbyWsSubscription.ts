import { useWebSocketContext } from "@/context/WebSocketProvider";
import { handleLobbyWebSocketMessages } from "@/services/websocket/handleLobbyWebSocketMessages";
import type { QueryClient } from "@tanstack/react-query";
import { useEffect } from "react";
import type { NavigateFunction } from "react-router-dom";

export function useHandleLobbyWsSubscription(
    lobbyId: string | null | undefined,
    queryClient: QueryClient,
    navigate: NavigateFunction
) {
    const { subscribe, unsubscribe } = useWebSocketContext();

    // Subscribe user to lobby websocket topic - ensures when page is refreshed or new session starts, user continues to receive lobby updates 
    useEffect(() => {
        if (!lobbyId) return;
        const lobbyIdNum = parseInt(lobbyId);
        const topic = `/topic/lobby/${lobbyId}`;
        const subscription = subscribe(topic, (body: any) => handleLobbyWebSocketMessages(body, queryClient, lobbyIdNum, navigate));

        // Call cleanup function to unsubscribe when component unmounts (user leaves the page)
        return () => {
            if (subscription) unsubscribe(topic);
        };
    }, [lobbyId]);
}