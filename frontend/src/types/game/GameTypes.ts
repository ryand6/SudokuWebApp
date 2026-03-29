import type { GamePlayerSettingsDto } from "../dto/entity/game/GamePlayerSettingsDto";
import type { SharedGameStateDto } from "../dto/entity/game/SharedGameStateDto";
import type { Difficulty } from "../enum/Difficulty";
import type { GameMode } from "../enum/GameMode";
import type { GameResult } from "../enum/GameResult";
import type { GameStatus } from "../enum/GameStatus";
import type { PlayerColour } from "../enum/PlayerColour";
import type { TimeLimitPreset } from "../enum/TimeLimitPreset";


export type PrivateCellState = {
    value: string | undefined;
    notes: number;
    isRejected: boolean
}

export type SharedCellState = {
    value: string | undefined;
};

export type PrivateBoardState = PrivateCellState[][];

export type SharedBoardState = SharedCellState[][];

export type CellCoordinates = {
    row: number,
    col: number
}

export type CellHighlightDetails = {
    row: number,
    col: number,
    colour: PlayerColour
}

export type GamePlayer = {
    name: string,
    colour: PlayerColour,
    boardProgress: boolean[],
    score: number,
    firsts: number,
    mistakes: number,
    gameLoaded: boolean,
    gameResult: GameResult
};

// Key represents playerId
export type GamePlayers = Record<number, GamePlayer>;

// Public data stored in shared cache
export type PublicGameState = {
    gameId: number,
    playerIds: number[],
    players: GamePlayers,
    sharedGameState: SharedGameState,
    initialBoardState: string,
    gameMode: GameMode,
    difficulty: Difficulty,
    timeLimit: TimeLimitPreset,
    gameStatus: GameStatus,
    gameStartsAt: string | null,
    gameEndsAt: string | null
}

export type SharedGameState = {
    cellFirstOwnership: Record<number, number>,
    currentSharedBoardState?: SharedBoardState
}

// Requested separately from backend, stored in user's personal game cache
export type PrivateGamePlayerState = {
    boardState: PrivateBoardState,
    currentStreak: number,
    activeMultiplier: number,
    multiplierEndsAt: string | null,
    settings: GamePlayerSettingsDto
}