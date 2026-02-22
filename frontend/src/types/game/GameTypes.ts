import type { PlayerColour } from "../enum/PlayerColour";

export type CellState = {
    value: string | undefined;
    notes: number;
};

export type BoardState = CellState[][];

// number represents playerId
export type BoardStates = Record<number, BoardState>;

export type PlayerState = {
    name: string,
    colour: PlayerColour
};

// Key represents playerId
export type PlayerStates = Record<number, PlayerState>;

export type GameState = {
    gameId: number,
    playerIds: number[],
    players: PlayerStates,
    gameStates: BoardStates
}