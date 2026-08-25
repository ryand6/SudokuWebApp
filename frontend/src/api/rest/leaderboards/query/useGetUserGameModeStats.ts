import { queryKeys } from "@/state/queryKeys";
import type { LeaderboardsDto } from "@/types/dto/entity/leaderboards/LeaderboardsDto";
import type { GameMode } from "@/types/enum/GameMode";
import { useQuery } from "@tanstack/react-query";
import { getUserGameModeStats } from "./getUserGameModeStats";

export function useGetUserGameModeStats(gameMode: GameMode) {
    return useQuery<LeaderboardsDto, Error>({
        queryKey: queryKeys.gameModeStats(gameMode),
        queryFn: () => getUserGameModeStats(gameMode),
        retry: false,
        staleTime: 10000
    })
}