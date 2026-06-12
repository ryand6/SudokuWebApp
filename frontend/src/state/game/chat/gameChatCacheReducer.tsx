import type { InfiniteData } from "@tanstack/react-query";
import type { GameChatEvent } from "../gameEvents";
import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { handleNewInfiniteData } from "@/utils/game/infiniteDataUtils";
import { toast } from "react-toastify";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import { GameChatToast } from "@/components/game/hud/modals/chat/GameChatToast";

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
                toast(
                    <GameChatToast
                        username={messageDto.username}
                        message={messageDto.message}
                        playerColour={playerColours[messageDto.userId]}
                    />, {
                    containerId: "default",
                    autoClose: 1500,
                    hideProgressBar: true,
                    className: "!w-auto !bg-background !border !border-border !rounded-bl-xl !rounded-br-xl !rounded-tl-xl !rounded-tr-xs !shadow-lg !p-2 !min-h-0"
                });
            }
            return handleNewInfiniteData<GameChatMessageDto>(existingData, event);
        }
        default:
            return existingData;
    }
}