import type { PlayerColour } from "@/types/enum/PlayerColour"
import type { UserDto } from "./UserDto"

export type GameStateDto = {
    id: number,
    gameId: number,
    user: UserDto,
    score: number,
    playerColour: PlayerColour,
    currentBoardState: string,
    // 16 bit array representing bitmask of each cell's notes
    notes: Uint16Array
}