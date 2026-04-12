import type { LobbyChatMessageDto } from "@/types/dto/entity/lobby/LobbyChatMessageDto";
import { getLocalTime } from "@/utils/time/getLocalTime";

export function LobbyChatMessage({
    msg
}: {
    msg: LobbyChatMessageDto
}) {
    return (
        <div id="lobby-message-container" className="flex flex-row m-2">
            <div id="lobby-message-user" className="w-[25%]">
                {msg.username}
            </div>
            <div id="lobby-message-content" className="w-[65%] whitespace-pre-line">
                {msg.message}
            </div>
            <div id="lobby-message-timestamp" className="W-[10%]">
                {getLocalTime(msg.createdAt)}
            </div>
        </div>
    );
}