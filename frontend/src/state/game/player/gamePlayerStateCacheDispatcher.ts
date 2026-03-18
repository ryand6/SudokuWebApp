import type { QueryClient } from "@tanstack/react-query";
import type { GamePlayerStateEvent } from "../gameEvents";
import { queryKeys } from "@/state/queryKeys";
import type { PrivateGamePlayerState } from "@/types/game/GameTypes";
import { gamePlayerStateCacheReducer } from "./gamePlayerStateCacheReducer";

export function gamePlayerStateCacheDispatcher(
    queryClient: QueryClient,
    gameId: number,
    userId: number,
    event: GamePlayerStateEvent
) {
    queryClient.setQueryData<PrivateGamePlayerState>(queryKeys.gamePlayerState(gameId, userId), (old: PrivateGamePlayerState | undefined) => {
        if (!old) return old;
        return gamePlayerStateCacheReducer(old, event);
    })
}