import type { GameEventDto } from "../entity/game/GameEventDto"

export type GetGameEventsResponseDto = {
    gameEvents: GameEventDto[]
}