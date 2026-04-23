import type { InfiniteData } from "@tanstack/react-query";
import type { GameChatEvent } from "../gameEvents";
import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { handleNewInfiniteData } from "@/utils/game/infiniteDataUtils";
import { toast } from "react-toastify";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { toastColourMap } from "@/utils/game/gameColourUtils";

export function gameChatCacheReducer(
    existingData: InfiniteData<GameChatMessageDto[]> | undefined,
    userId: number,
    gameNotificationsEnabled: boolean,
    playerColours: Record<number, PlayerColour>,
    event: GameChatEvent
) {

    switch (event.type) {
        case "GAME_CHAT_MESSAGE": {
            const messageDto: GameChatMessageDto = event.newMessage;

            if (messageDto.userId !== userId && gameNotificationsEnabled) {
                toast(`${messageDto.username}: ${messageDto.message}`, {
                    containerId: "default",
                    style: {
                        background: toastColourMap[playerColours[messageDto.userId]],
                        border: "1px solid var(--border)",
                        color: "var(--popover-foreground)"
                    }
                });
            }
            return handleNewInfiniteData<GameChatMessageDto>(existingData, event);
        }
        default:
            return existingData;
    }
}