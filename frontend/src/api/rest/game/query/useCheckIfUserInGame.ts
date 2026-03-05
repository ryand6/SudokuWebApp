import { checkIfUserInGame } from "@/api/rest/game/query/checkIfUserInGame";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { GameState } from "@/types/game/GameTypes";
import { normaliseGameState } from "@/utils/game/normaliseGameState";
import { useQuery } from "@tanstack/react-query";

export function useCheckIfUserInGame(gameId: number, userId: number) {
    return useQuery<GameState>({
        queryKey: ["game", gameId],
        queryFn: async () => {
            const gameData: GameDto = await checkIfUserInGame(gameId, userId);
            return normaliseGameState(gameData);
        },
        staleTime: 0,
        // does not run if default values are given (-1)
        enabled: gameId > 0 && userId > 0
    })
}