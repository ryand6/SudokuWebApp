import type { GameChatMessageDto } from "@/types/dto/entity/game/GameChatMessageDto";
import { getLocalTime } from "@/utils/time/getLocalTime";

export function GameChatMessage({
    msg
}: {
    msg: GameChatMessageDto
}) {
    return (
        <div id="game-message-container" className="flex flex-row m-2">
            <div id="game-message-user" className="w-[25%]">
                {msg.username}
            </div>
            <div id="game-message-content" className="w-[65%] whitespace-pre-line">
                {msg.message}
            </div>
            <div id="game-message-timestamp" className="W-[10%]">
                {getLocalTime(msg.createdAt)}
            </div>
        </div>
    );
}