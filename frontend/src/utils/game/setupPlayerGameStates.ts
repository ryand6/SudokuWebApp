import type { PlayerState, CellState } from "@/pages/GamePage";
import type { GameDto } from "@/types/dto/entity/GameDto";
import type { GameStateDto } from "@/types/dto/entity/GameStateDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import type { Dispatch, SetStateAction } from "react";
import { CELL_COUNT, GRID_SIZE } from "./gameConstants";

export function setupPlayerGameStates(
    gameData: GameDto | undefined, 
    currentUser: UserDto | undefined, 
    setGameState: Dispatch<SetStateAction<PlayerState | undefined>>,
    setRivalGameStates: Dispatch<SetStateAction<PlayerState[] | undefined>>
) {
    if (!gameData || !currentUser) return;

    const userPlayerState: PlayerState | undefined = gameData.gameStates.filter((gs) => gs.user.id === currentUser.id).map((gs) => fillPlayerState(gs)).at(0);

    if (!userPlayerState) {
        throw new Error(`Unable to create player state for user with id ${currentUser.id}`);
    }

    const rivalPlayerStates: PlayerState[] = gameData.gameStates.filter((gs) => gs.user.id !== currentUser.id).map((gs) => fillPlayerState(gs));

    if (rivalPlayerStates.length !== (gameData.gameStates.length - 1)) {
        throw new Error(`Expected ${gameData.gameStates.length - 1} rival player states to be created, instead got ${rivalPlayerStates.length}`);
    }

    setGameState(userPlayerState);

    setRivalGameStates(rivalPlayerStates);
}

function fillPlayerState(gameState: GameStateDto): PlayerState {
    return {
        id: gameState.user.id,
        name: gameState.user.username,
        colour: gameState.playerColour,
        boardState: fillBoardState(gameState)
    }
}

function fillBoardState(gameState: GameStateDto): CellState[][] {

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