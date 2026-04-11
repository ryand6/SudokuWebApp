import type { InfiniteData } from "@tanstack/react-query";
import type { LobbyChatEvent } from "../lobbyEvents";
import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import { handleNewInfiniteData } from "@/utils/game/infiniteDataUtils";

export function lobbyChatCacheReducer(
    existingData: InfiniteData<LobbyChatMessageDto[]> | undefined,
    event: LobbyChatEvent
) {
    switch (event.type) {
        case "LOBBY_CHAT_MESSAGE": {
            return handleNewInfiniteData<LobbyChatMessageDto>(existingData, event);
        }
        default:
            return existingData;
    }
}