import { checkIfUserInGame } from "@/api/rest/game/query/checkIfUserInGame";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { PublicGameState } from "@/types/game/GameTypes";
import { normalisePublicGameData } from "@/utils/game/normaliseGameState";
import { useQuery } from "@tanstack/react-query";

export function useCheckIfUserInGame(gameId: number) {
    return useQuery<PublicGameState>({
        queryKey: ["game", gameId],
        queryFn: async () => {
            const gameData: GameDto = await checkIfUserInGame(gameId);
            return normalisePublicGameData(gameData);
        },
        staleTime: 0,
        retry: false,
        enabled: gameId > 0
    })
}