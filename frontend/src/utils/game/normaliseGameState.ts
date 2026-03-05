import type { GamePlayerStateDto } from "@/types/dto/entity/game/GamePlayerStateDto";
import { CELL_COUNT, GRID_SIZE } from "./gameConstants";
import type { BoardStates, CellState, GameState, PlayerStates } from "@/types/game/GameTypes";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { GamePlayerStateDtoRaw } from "@/types/dto/entity/game/GamePlayerStateDtoRaw";
import { mapGameState } from "./mapGameState";

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


export function fillBoardState(gameState: GamePlayerStateDtoRaw): CellState[][] {

    if (gameState.currentBoardState.length != CELL_COUNT) {
        throw new Error(`Expected board state string to be ${CELL_COUNT} characters long. Instead got ${gameState.currentBoardState.length} characters.`)
    }

    const normalisedGameState: GamePlayerStateDto = mapGameState(gameState);

    const boardState = [];

    for (let y = 0; y < GRID_SIZE; y++) {
        let boardRow: CellState[] = [];
        for (let x = 0; x < GRID_SIZE; x++) {
            // Map 2D co-ords to 1D array positions
            let value = normalisedGameState.currentBoardState.at((y * 9) + x);
            if (value === "0") {
                value = undefined;
            }
            let notes = normalisedGameState.notes[(y * 9) + x];
            boardRow[x] = {value: value, notes: notes};
        }
        boardState.push(boardRow);
    }

    return boardState;
}