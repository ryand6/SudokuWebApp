import type { GameStatus } from '@/types/enum/GameStatus.ts';
import type { SharedGameStateDto } from './SharedGameStateDto';
import type { GamePlayerDto } from './GamePlayerDto';
import type { GameSettingsDto } from './GameSettingsDto';

export type GameDto = {
    gameId: number,
    lobbyId: number,
    gamePlayers: GamePlayerDto[],
    sharedGameState: SharedGameStateDto,
    initialBoardState: string,
    gameSettings: GameSettingsDto,
    gameStatus: GameStatus,
    gameStartsAt: string | null,
    gameEndsAt: string | null,
    endedPrematurely: boolean,
    gameEndedAt: string | null
}