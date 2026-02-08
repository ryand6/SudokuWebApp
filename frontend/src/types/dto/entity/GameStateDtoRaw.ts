import type { UserDto } from './UserDto';
import type { PlayerColour } from '../../enum/PlayerColour';

export type GameStateDtoRaw = {
    id: number,
    gameId: number,
    user: UserDto,
    score: number,
    playerColour: PlayerColour,
    currentBoardState: string,
    // Base64 encoded representation
    notes: string
}