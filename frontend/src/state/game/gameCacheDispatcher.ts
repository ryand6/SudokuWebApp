import type { QueryClient } from "@tanstack/react-query";
import type { GameEvent } from "./gameEvents";
import { queryKeys } from "../queryKeys";
import type { PublicGameState } from "@/types/game/GameTypes";
import { gameCacheReducer } from "./gameCacheReducer";

export function gameCacheDispatcher(
    queryClient: QueryClient,
    gameId: number,
    event: GameEvent
) {
    queryClient.setQueryData<PublicGameState>(queryKeys.game(gameId), (old: PublicGameState | undefined) => {
        if (!old) return old;
        return gameCacheReducer(old, event);
    })
}