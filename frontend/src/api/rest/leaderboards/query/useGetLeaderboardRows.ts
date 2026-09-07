import { queryKeys } from "@/state/queryKeys";
import { useInfiniteQuery } from "@tanstack/react-query";
import { PAGE_SIZE } from "@/utils/global/globalConstants";
import type { LeaderboardRow } from "@/types/dto/entity/leaderboards/LeaderboardRow";
import { getLeaderboardRows } from "./getLeaderboardRows";
import type { GameMode } from "@/types/enum/GameMode";

export function useGetLeaderboardRows(gameMode: GameMode) { 
    return useInfiniteQuery<LeaderboardRow[], Error>({ 
        queryKey: queryKeys.leaderboardRows(gameMode),
        queryFn: async ({ queryKey, pageParam }) => {
            const [, gameMode] = queryKey;
            const dto = await getLeaderboardRows(gameMode as GameMode, pageParam as number);
            return dto;
        },
        initialPageParam: 0,
        // Pages use zero-based indexing, therefore next page is equal to the length of the current page array (containing all pages retrieved so far) rather than incrementing the length by 1
        getNextPageParam: (lastPage, allPages) => lastPage?.length === PAGE_SIZE ? allPages.length : undefined,
    });
}