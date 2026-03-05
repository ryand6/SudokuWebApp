import { getGame } from "@/api/rest/game/query/getGame";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { GameState } from "@/types/game/GameTypes";
import { normaliseGameState } from "@/utils/game/normaliseGameState";
import { useQuery } from "@tanstack/react-query";

export function useGetGame(gameId: number) {
    return useQuery<GameState>({
        queryKey: ["game", gameId],
        queryFn: async () => {
            const gameData: GameDto = await getGame(gameId);
            return normaliseGameState(gameData);
        },
        // Only run if gameId has a value
        enabled: !!gameId,
        retry: false,
        // cache is updated by websocket connection, therefore refetching data not required
        staleTime: Infinity,
    })
}