import type { GameEventDto } from "@/types/dto/entity/game/GameEventDto";

export function GameEventMessage({
    msg
}: {
    msg: GameEventDto
}) {
    return (
        <div id="game-event-message-container" className="flex flex-row m-2">
            <div id="lobby-message-user" className="w-[30%]">
                {msg.userId}
            </div>
            <div id="lobby-message-content" className="w-[70%] whitespace-pre-line">
                {msg.message}
            </div>
        </div>
    );
}