import type { MessageType } from "@/types/enum/MessageType"

export type GameChatMessageDto = {
    id: number,
    gameId: number,
    userId: number,
    username: string,
    message: string,
    messageType: MessageType,
    createdAt: string
}