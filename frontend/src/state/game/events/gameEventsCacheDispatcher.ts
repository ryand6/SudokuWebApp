import type { InfiniteData, QueryClient } from "@tanstack/react-query";
import type { GameEventLog } from "../gameEvents";
import type { GameEventDto } from "@/types/dto/entity/game/GameEventDto";
import { queryKeys } from "@/state/queryKeys";
import { gameEventsCacheReducer } from "./gameEventsCacheReducer";

export function gameEventsCacheDispatcher(
    queryClient: QueryClient,
    gameId: number,
    event: GameEventLog
) {
    queryClient.setQueryData<InfiniteData<GameEventDto[]>>(queryKeys.gameEvents(gameId), (old: InfiniteData<GameEventDto[]> | undefined) => {
        return gameEventsCacheReducer(old, event);
    })
}