import type { GameDto } from "@/types/dto/entity/GameDto";
import type { GameStateDto } from "@/types/dto/entity/GameStateDto";
import { CELL_COUNT, GRID_SIZE } from "./gameConstants";
import type { BoardStates, CellState, GameState, PlayerStates } from "@/types/game/GameTypes";

export function normaliseGameState(
    gameData: GameDto
): GameState {
    const playerIds: number[] = [];
    const players: PlayerStates = {};
    const gameStates: BoardStates = {};

    gameData.gameStates.forEach((gs) => {
        playerIds.push(gs.user.id);

        players[gs.user.id] = {
            name: gs.user.username,
            colour: gs.playerColour
        };

        gameStates[gs.user.id] = fillBoardState(gs);
    });

    playerIds.sort();

    const gameState: GameState = {
        gameId: gameData.id,
        playerIds: playerIds,
        players: players,
        gameStates: gameStates
    };

    return gameState;
}


export function fillBoardState(gameState: GameStateDto): CellState[][] {

    if (gameState.currentBoardState.length != CELL_COUNT) {
        throw new Error(`Expected board state string to be ${CELL_COUNT} characters long. Instead got ${gameState.currentBoardState.length} characters.`)
    }

    const boardState = [];

    for (let y = 0; y < GRID_SIZE; y++) {
        let boardRow: CellState[] = [];
        for (let x = 0; x < GRID_SIZE; x++) {
            // Map 2D co-ords to 1D array positions
            let value = gameState.currentBoardState.at((y * 9) + x);
            if (value === "0") {
                value = undefined;
            }
            let notes = gameState.notes[(y * 9) + x];
            boardRow[x] = {value: value, notes: notes};
        }
        boardState.push(boardRow);
    }

    return boardState;
}