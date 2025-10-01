import type { ScoreDto } from './ScoreDto';

export type UserDto = {
    id: number,
    username: string,
    isOnline: boolean,
    score: ScoreDto
}