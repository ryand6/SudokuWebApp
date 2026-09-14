import { queryKeys } from "@/state/queryKeys";
import type { LeaderboardRowDto } from "@/types/dto/entity/leaderboards/LeaderboardRowDto";
import type { GameMode } from "@/types/enum/GameMode";
import { useQuery } from "@tanstack/react-query";
import { getUserLeaderboardRow } from "./getUserLeaderboardRow";

export function useGetUserLeaderboardRow(userId: number, gameMode: GameMode) {
    return useQuery<LeaderboardRowDto, Error>({
        queryKey: queryKeys.userLeaderboardRow(userId, gameMode),
        queryFn: () => getUserLeaderboardRow(gameMode),
        retry: false,
        staleTime: 10000
    })
}