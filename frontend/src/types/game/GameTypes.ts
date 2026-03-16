import type { GamePlayerSettingsDto } from "../dto/entity/game/GamePlayerSettingsDto";
import type { SharedGameStateDto } from "../dto/entity/game/SharedGameStateDto";
import type { Difficulty } from "../enum/Difficulty";
import type { GameMode } from "../enum/GameMode";
import type { GameResult } from "../enum/GameResult";
import type { GameStatus } from "../enum/GameStatus";
import type { PlayerColour } from "../enum/PlayerColour";
import type { TimeLimitPreset } from "../enum/TimeLimitPreset";

export type CellState = {
    value: string | undefined;
    notes: number;
};

export type BoardState = CellState[][];

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
    gameLoaded: boolean,
    gameResult: GameResult,
    currentHighlightedCell: CellCoordinates | null
};

// Key represents playerId
export type GamePlayers = Record<number, GamePlayer>;

// Public data stored in shared cache
export type PublicGameState = {
    gameId: number,
    playerIds: number[],
    players: GamePlayers,
    sharedGameState: SharedGameStateDto | null,
    initialBoardState: string,
    gameMode: GameMode,
    difficulty: Difficulty,
    timeLimit: TimeLimitPreset,
    gameStatus: GameStatus,
    gameStartsAt: string | null,
    gameEndsAt: string | null
}

// Requested separately from backend, stored in user's personal game cache
export type PrivateGamePlayerState = {
    boardState: BoardState,
    currentStreak: number,
    activeMultiplier: number,
    multiplierEndsAt: string | null,
    settings: GamePlayerSettingsDto
}