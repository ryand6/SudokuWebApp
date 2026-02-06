import type { UserDto } from './UserDto';
import type { PlayerColour } from '../../enum/PlayerColour';

export type GameStateDto = {
    id: number,
    gameId: number,
    user: UserDto,
    score: number,
    playerColour: PlayerColour,
    currentBoardState: string,
    notes: string
}