import { getGame } from "@/api/rest/game/query/getGame";
import type { GameDto } from "@/types/dto/entity/GameDto";
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
        
        // // cache is updated by websocket connection, however to prevent websockets being the source of truth (incase critical updates are missed), allow revalidation to occur after 5 seconds
        // staleTime: 5000,
        // refetchOnWindowFocus: true,
        // refetchOnReconnect: true,
        // refetchInterval: (query): number | false => {
        //     const data = query.state.data;
        //     if (!data) return 1000; // game unknown or loading
        //     return 5000; // default polling of every 5 seconds
        // }
    })
}