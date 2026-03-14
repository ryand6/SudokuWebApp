import type { PrivateGamePlayerState } from "@/types/game/GameTypes";
import { useQuery } from "@tanstack/react-query";
import { getGamePlayerState } from "./getGamePlayerState";
import { normalisePrivateGameStateData } from "@/utils/game/normaliseGameState";
import type { PrivateGamePlayerStateDtoRaw } from "@/types/dto/entity/game/PrivateGamePlayerStateDtoRaw";
import { queryKeys } from "@/state/queryKeys";

export function useGetGamePlayerState(gameId: number, userId: number | undefined) {
    return useQuery<PrivateGamePlayerState>({
        queryKey: queryKeys.gamePlayerState(gameId, userId),
        queryFn: async () => {
            const gamePlayerStateData: PrivateGamePlayerStateDtoRaw = await getGamePlayerState(gameId);
            return normalisePrivateGameStateData(gamePlayerStateData);
        },
        // Only run if gameId has a value
        enabled: !!gameId && !!userId,
        retry: false,
        // cache is updated by websocket connection, therefore refetching data not required
        staleTime: Infinity,
    })
}