import type { LobbyChatMessageDto } from "../entity/LobbyChatMessageDto"

export type LobbyGetChatMessagesResponseDto = {
    messages: LobbyChatMessageDto[]
}