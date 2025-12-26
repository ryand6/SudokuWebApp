import { checkIfUserInGame } from "@/api/rest/game/checkIfUserInGame";
import type { GameDto } from "@/types/dto/entity/GameDto";
import { useQuery } from "@tanstack/react-query";

export function useCheckIfUserInGame(gameId: number, userId: number) {
    return useQuery<GameDto>({
        queryKey: ["game", gameId],
        queryFn: () => checkIfUserInGame(gameId, userId),
    })
}