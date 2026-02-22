import { getLobbyChatMessages } from "@/api/rest/lobbychat/query/getLobbyChatMessages";
import type { LobbyChatMessageDto } from "@/types/dto/entity/LobbyChatMessageDto";
import { useInfiniteQuery } from "@tanstack/react-query";

export const PAGE_SIZE = 100;

export function useGetLobbyChatMessages(lobbyId: number) { 
    return useInfiniteQuery<LobbyChatMessageDto[], Error>({ 
        queryKey: ["lobbyChat", lobbyId],
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