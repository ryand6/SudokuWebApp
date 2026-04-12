import type { InfiniteData } from "@tanstack/react-query";
import type { GameChatEvent } from "../gameEvents";
import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { handleNewInfiniteData } from "@/utils/game/infiniteDataUtils";

export function gameChatCacheReducer(
    existingData: InfiniteData<GameChatMessageDto[]> | undefined,
    event: GameChatEvent
) {
    switch (event.type) {
        case "GAME_CHAT_MESSAGE": {
            return handleNewInfiniteData<GameChatMessageDto>(existingData, event);
        }
        default:
            return existingData;
    }
}