import { queryKeys } from "@/state/queryKeys";
import type { LeaderboardRow } from "@/types/dto/entity/leaderboards/LeaderboardRow";
import type { GameMode } from "@/types/enum/GameMode";
import { useQuery } from "@tanstack/react-query";
import { getUserLeaderboardRow } from "./getUserLeaderboardRow";

export function useGetUserLeaderboardRow(userId: number, gameMode: GameMode) {
    return useQuery<LeaderboardRow, Error>({
        queryKey: queryKeys.userLeaderboardRow(userId, gameMode),
        queryFn: () => getUserLeaderboardRow(gameMode),
        retry: false,
        staleTime: 10000
    })
}