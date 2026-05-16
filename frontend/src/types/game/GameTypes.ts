import type { GameSettingsDto } from "../dto/entity/game/GameSettingsDto";
import type { CellStatus } from "../enum/CellStatus";
import type { GameResult } from "../enum/GameResult";
import type { GameStatus } from "../enum/GameStatus";
import type { PlayerColour } from "../enum/PlayerColour";


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
    boardProgress: CellStatus[],
    score: number,
    firsts: number,
    mistakes: number,
    maxStreak: number,
    gameLoaded: boolean,
    gameResult: GameResult,
    finishedGame: boolean,
    finishedGameTimestamp: string | null
};

// Key represents playerId
export type GamePlayers = Record<number, GamePlayer>;

// Public data stored in shared cache
export type PublicGameState = {
    gameId: number,
    lobbyId: number,
    playerIds: number[],
    players: GamePlayers,
    sharedGameState: SharedGameState,
    initialBoardState: string,
    gameSettings: GameSettingsDto,
    gameStatus: GameStatus,
    gameStartsAt: string | null,
    gameEndsAt: string | null,
    endedPrematurely: boolean
}

export type SharedGameState = {
    cellFirstOwnership: Record<number, number>,
    currentSharedBoardState?: SharedBoardState
}

export type LeaderboardResult = {
    score: number,
    cellsCompleted: number,
    scoreOverCellsCompleted: number,
    normalisationRate: number,
    normalisedScore: number,
    difficultyMultiplier: number,
    scoreTimesDifficultyMultiplier: number,
    timerMultiplier: number,
    scoreTimesTimerMultiplier: number,
    finalScore: number
}

// Requested separately from backend, stored in user's personal game cache
export type PrivateGamePlayerState = {
    boardState: PrivateBoardState,
    currentStreak: number,
    activeMultiplier: number,
    multiplierEndsAt: string | null,
    leaderboardResult: LeaderboardResult | undefined
}