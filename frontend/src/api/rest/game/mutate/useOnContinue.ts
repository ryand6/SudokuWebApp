import { queryKeys } from "@/state/queryKeys";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { onContinue } from "./onContinue";
import { normalisePublicGameData } from "@/utils/game/normaliseGameState";
import type { PrivateGamePlayerState } from "@/types/game/GameTypes";

export function useOnContinue(userId: number) {
    const queryClient = useQueryClient();

    return useMutation<GameDto, Error, number>({
        mutationFn: (gameId: number) => onContinue(gameId),
        onSuccess: (data) => {
            queryClient.setQueryData(queryKeys.game(data.gameId), normalisePublicGameData(data));
            queryClient.setQueryData(
                queryKeys.gamePlayerState(data.gameId, userId), 
                (oldData: PrivateGamePlayerState) => oldData ? {
                    ...oldData,
                    leaderboardResult: undefined
                } : oldData
            )
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}