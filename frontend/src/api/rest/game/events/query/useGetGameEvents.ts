import { queryKeys } from "@/state/queryKeys";
import { useInfiniteQuery } from "@tanstack/react-query";
import { PAGE_SIZE } from "@/utils/global/globalConstants";
import { getGameEvents } from "./getGameEvents";
import type { GameEventDto } from "@/types/dto/entity/game/GameEventDto";

export function useGetGameEvents(gameId: number) { 
    return useInfiniteQuery<GameEventDto[], Error>({ 
        queryKey: queryKeys.gameEvents(gameId),
        queryFn: async ({ queryKey, pageParam }) => {
            const [, gameId] = queryKey;
            const dto = await getGameEvents(gameId as number, pageParam as number);
            return dto.gameEvents;
        },
        initialPageParam: 0,
        // Pages use zero-based indexing, therefore next page is equal to the length of the current page array (containing all pages retrieved so far) rather than incrementing the length by 1
        getNextPageParam: (lastPage, allPages) => lastPage?.length === PAGE_SIZE ? allPages.length : undefined,
    });
}