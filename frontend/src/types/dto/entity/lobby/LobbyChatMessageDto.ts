import type { MessageType } from "@/types/enum/MessageType"

export type LobbyChatMessageDto = {
    id: number,
    lobbyId: number,
    userId: number,
    username: string,
    message: string,
    messageType: MessageType,
    createdAt: string
}