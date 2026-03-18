import { useWebSocketContext } from "@/context/WebSocketProvider";
import { handleGamePlayerStateWebSocketMessages } from "@/services/websocket/handleGamePlayerStateWebSocketMessages";
import { handleGameWebSocketMessages } from "@/services/websocket/handleGameWebSocketMessages";
import type { CellCoordinates } from "@/types/game/GameTypes";
import type { QueryClient } from "@tanstack/react-query";
import { useEffect, type Dispatch, type SetStateAction } from "react";
import type { NavigateFunction } from "react-router-dom";

export function useHandleGameWsSubscriptions(
    gameId: string | null | undefined,
    userId: number | null | undefined,
    queryClient: QueryClient,
    navigate: NavigateFunction,
    setGameHighlightedCells: Dispatch<SetStateAction<Map<number, CellCoordinates> | undefined>>
) {
    const { subscribe, unsubscribe } = useWebSocketContext();

    useEffect(() => {
        if (!gameId || !userId) return;
        const gameIdNum = parseInt(gameId);
        const gameTopic = `/topic/game/${gameId}`;
        const gameSubscription = subscribe(gameTopic, (body: any) => handleGameWebSocketMessages(body, queryClient, gameIdNum, navigate, setGameHighlightedCells));

        const gamePlayerStateTopic = `/topic/game/${gameId}/player/${userId}`;
        const gamePlayerStateSubscription = subscribe(gameTopic, (body: any) => handleGamePlayerStateWebSocketMessages(body, queryClient, gameIdNum, userId, navigate));

        return () => {
            if (gameSubscription) unsubscribe(gameTopic);
            if (gamePlayerStateSubscription) unsubscribe(gamePlayerStateTopic);
        }
    }, [gameId, userId]);
}