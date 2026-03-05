import type { LobbyChatMessageDto } from "../entity/lobby/LobbyChatMessageDto"

export type LobbyGetChatMessagesResponseDto = {
    lobbyChatMessages: LobbyChatMessageDto[]
}