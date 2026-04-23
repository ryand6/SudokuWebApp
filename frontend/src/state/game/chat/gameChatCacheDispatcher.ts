import type { InfiniteData, QueryClient } from "@tanstack/react-query";
import type { GameChatEvent } from "../gameEvents";
import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { queryKeys } from "@/state/queryKeys";
import { gameChatCacheReducer } from "./gameChatCacheReducer";
import type { PlayerColour } from "@/types/enum/PlayerColour";

export function gameChatCacheDispatcher(
    queryClient: QueryClient,
    gameId: number,
    userId: number,
    gameNotificationsEnabled: boolean,
    playerColours: Record<number, PlayerColour>,
    event: GameChatEvent
) {
    queryClient.setQueryData<InfiniteData<GameChatMessageDto[]>>(queryKeys.gameChat(gameId), (old: InfiniteData<GameChatMessageDto[]> | undefined) => {
        return gameChatCacheReducer(old, userId, gameNotificationsEnabled, playerColours, event) as InfiniteData<GameChatMessageDto[], unknown> | undefined;
    })
}