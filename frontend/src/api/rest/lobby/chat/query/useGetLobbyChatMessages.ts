import { queryKeys } from "@/state/queryKeys";
import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import { useInfiniteQuery } from "@tanstack/react-query";
import { getLobbyChatMessages } from "./getLobbyChatMessages";

export const PAGE_SIZE = 100;

export function useGetLobbyChatMessages(lobbyId: number) { 
    return useInfiniteQuery<LobbyChatMessageDto[], Error>({ 
        queryKey: queryKeys.lobbyChat(lobbyId),
        queryFn: async ({ queryKey, pageParam }) => {
            const [, lobbyId] = queryKey;
            const dto = await getLobbyChatMessages(lobbyId as number, pageParam as number);
            return dto.lobbyChatMessages;
        },
        initialPageParam: 0,
        // Pages use zero-based indexing, therefore next page is equal to the length of the current page array (containing all pages retrieved so far) rather than incrementing the length by 1
        getNextPageParam: (lastPage, allPages) => lastPage?.length === PAGE_SIZE ? allPages.length : undefined,
    });
}