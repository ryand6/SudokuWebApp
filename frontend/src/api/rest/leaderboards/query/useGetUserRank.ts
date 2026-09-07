import { queryKeys } from "@/state/queryKeys";
import type { GameMode } from "@/types/enum/GameMode";
import { useQuery } from "@tanstack/react-query";
import { getUserRank } from "./getUserRank";

export function useGetUserRank(userId: number, gameMode: GameMode) {
    return useQuery<number, Error>({
        queryKey: queryKeys.userRank(userId, gameMode),
        queryFn: () => getUserRank(gameMode),
        retry: false,
        staleTime: 0
    })
}