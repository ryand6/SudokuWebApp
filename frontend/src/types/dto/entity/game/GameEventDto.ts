import type { GameEventType } from "@/types/enum/GameEventType"

export type GameEventDto = {
    gameId: number,
    userId: number,
    username: string,
    eventType: GameEventType,
    message: string,
    sequenceNumber: number
}
