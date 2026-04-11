import type { InfiniteData, QueryClient } from "@tanstack/react-query";
import type { LobbyChatEvent } from "../lobbyEvents";
import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import { queryKeys } from "@/state/queryKeys";
import { lobbyChatCacheReducer } from "./lobbyChatCacheReducer";

export function lobbyChatCacheDispatcher(
    queryClient: QueryClient,
    lobbyId: number,
    event: LobbyChatEvent
) {
    queryClient.setQueryData<InfiniteData<LobbyChatMessageDto[]>>(queryKeys.lobbyChat(lobbyId), (old: InfiniteData<LobbyChatMessageDto[]> | undefined) => {
        return lobbyChatCacheReducer(old, event) as InfiniteData<LobbyChatMessageDto[], unknown> | undefined;
    })
}