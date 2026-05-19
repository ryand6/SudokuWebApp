import type { MessageType } from "@/types/enum/MessageType";

export function sendGameChatMessage(
    send: (dest: string, body: any) => void,
    gameId: number,
    message: string,
    messageType: MessageType
) {
    send(`/app/game/${gameId}/chat/send-message`, {message: message, messageType: messageType})
}