import { queryKeys } from "@/state/queryKeys";
import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { useInfiniteQuery } from "@tanstack/react-query";
import { getGameChatMessages } from "./getGameChatMessages";
import { PAGE_SIZE } from "@/utils/global/globalConstants";

export function useGetGameChatMessages(gameId: number) { 
    return useInfiniteQuery<GameChatMessageDto[], Error>({ 
        queryKey: queryKeys.gameChat(gameId),
        queryFn: async ({ queryKey, pageParam }) => {
            const [, gameId] = queryKey;
            const dto = await getGameChatMessages(gameId as number, pageParam as number);
            return dto.gameChatMessages;
        },
        initialPageParam: 0,
        // Pages use zero-based indexing, therefore next page is equal to the length of the current page array (containing all pages retrieved so far) rather than incrementing the length by 1
        getNextPageParam: (lastPage, allPages) => lastPage?.length === PAGE_SIZE ? allPages.length : undefined,
    });
}