import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import { getLocalTime } from "@/utils/time/getLocalTime";

export function LobbyInfoMessage({
    msg
}: {
    msg: LobbyChatMessageDto
}) {
    return (
        <div id="lobby-info-message-container" className="flex flex-row m-1 text-sm">
            <div id="lobby-info-message-content" className="w-[90%]">
                <span>{msg.username} </span>
                <span className="whitespace-pre-line">{msg.message}</span>
            </div>
            <div id="lobby-info-message-timestamp" className="W-[10%]">
                {getLocalTime(msg.createdAt)}
            </div>
        </div>
    )
}