import type { TopFiveLeaderboardRow } from "@/types/dto/response/TopFiveWithUserRankDto";
import type { GameMode } from "@/types/enum/GameMode";
import { useQuery } from "@tanstack/react-query";
import { getTopFiveWithUserRank } from "./getTopFiveWithUserRank";
import { queryKeys } from "@/state/queryKeys";

export function useGetTopFiveWithUserRank(gameMode: GameMode) {
    return useQuery<TopFiveLeaderboardRow[], Error>({
        queryKey: queryKeys.topFiveWithUserRank(gameMode),
        queryFn: () => getTopFiveWithUserRank(gameMode),
        retry: false,
        staleTime: 10000
    })
}