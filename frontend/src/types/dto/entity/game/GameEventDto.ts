import type { GameEventType } from "@/types/enum/GameEventType"

export type GameEventDto = {
    gameId: number,
    userId: number,
    eventType: GameEventType,
    message: string,
    sequenceNumber: number
}
