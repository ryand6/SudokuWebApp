import type { Difficulty } from '@/types/enum/Difficulty.ts';
import type { TimeLimitPreset } from '@/types/enum/TimeLimitPreset.ts';
import type { GameStatus } from '@/types/enum/GameStatus.ts';
import type { GameMode } from '@/types/enum/GameMode.ts';
import type { SharedGameStateDto } from './SharedGameStateDto';
import type { GamePlayerDto } from './GamePlayerDto';

export type GameDto = {
    id: number,
    lobbyId: number,
    gamePlayers: GamePlayerDto[],
    sharedGameState: SharedGameStateDto | null,
    gameMode: GameMode,
    difficulty: Difficulty,
    timeLimit: TimeLimitPreset,
    gameStatus: GameStatus,
    gameStartsAt: string | null,
    gameEndsAt: string | null
}