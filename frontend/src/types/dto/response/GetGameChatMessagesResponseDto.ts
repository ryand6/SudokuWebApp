import type { GameChatMessageDto } from "../entity/game/GameChatMessageDto"

export type GetGameChatMessagesResponseDto = {
    gameChatMessages: GameChatMessageDto[]
}