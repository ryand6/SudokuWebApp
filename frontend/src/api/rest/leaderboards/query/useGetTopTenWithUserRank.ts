import type { TopTenLeaderboardRow } from "@/types/dto/response/TopTenWithUserRankDto";
import type { GameMode } from "@/types/enum/GameMode";
import { useQuery } from "@tanstack/react-query";
import { getTopTenWithUserRank } from "./getTopTenWithUserRank";
import { queryKeys } from "@/state/queryKeys";

export function useGetTopTenWithUserRank(gameMode: GameMode) {
    return useQuery<TopTenLeaderboardRow[], Error>({
        queryKey: queryKeys.topTenWithUserRank(gameMode),
        queryFn: () => getTopTenWithUserRank(gameMode),
        retry: false,
        staleTime: 10000
    })
}