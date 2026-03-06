import type { PrivateGamePlayerState } from "@/types/game/GameTypes";
import { useQuery } from "@tanstack/react-query";
import { getGamePlayerState } from "./getGamePlayerState";
import { normalisePrivateGameStateData } from "@/utils/game/normaliseGameState";
import type { PrivateGamePlayerStateDtoRaw } from "@/types/dto/entity/game/PrivateGamePlayerStateDtoRaw";

export function useGetGamePlayerState(gameId: number, userId: number | undefined) {
    return useQuery<PrivateGamePlayerState>({
        queryKey: ["game", gameId, "user", userId, "state"],
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