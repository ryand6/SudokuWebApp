import type { GamePlayerSettingsDto } from "../dto/entity/game/GamePlayerSettingsDto";
import type { SharedGameStateDto } from "../dto/entity/game/SharedGameStateDto";
import type { GameResult } from "../enum/GameResult";
import type { PlayerColour } from "../enum/PlayerColour";

export type CellState = {
    value: string | undefined;
    notes: number;
};

export type BoardState = CellState[][];

export type GamePlayer = {
    name: string,
    colour: PlayerColour,
    boardProgress: boolean[],
    score: number,
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
    sharedGameState: SharedGameStateDto | null,
}

// Requested separately from backend, stored in user's personal game cache
export type PrivateGamePlayerState = {
    boardState: BoardState,
    currentStreak: number,
    activeMultiplier: number,
    multiplierEndsAt: string | null,
    mySettings: GamePlayerSettingsDto
}