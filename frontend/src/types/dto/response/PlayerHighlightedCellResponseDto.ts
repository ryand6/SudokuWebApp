import type { CellCoordinates } from "@/types/game/GameTypes"

export type PlayerHighlightedCellResponseDto = {
    playerId: number,
    coordinates: CellCoordinates
}