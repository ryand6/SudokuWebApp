import { getGame } from "@/api/rest/game/query/getGame";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { PublicGameState } from "@/types/game/GameTypes";
import { normalisePublicGameData } from "@/utils/game/normaliseGameState";
import { useQuery } from "@tanstack/react-query";

export function useGetGame(gameId: number) {
    return useQuery<PublicGameState>({
        queryKey: ["game", gameId],
        queryFn: async () => {
            const gameData: GameDto = await getGame(gameId);
            return normalisePublicGameData(gameData);
        },
        // Only run if gameId has a value
        enabled: !!gameId,
        retry: false,
        // cache is updated by websocket connection, therefore refetching data not required
        staleTime: Infinity,
    })
}