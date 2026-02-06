import type { PlayerState, CellState } from "@/pages/GamePage";
import type { GameDto } from "@/types/dto/entity/GameDto";
import type { GameStateDto } from "@/types/dto/entity/GameStateDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import type { Dispatch, SetStateAction } from "react";

export function setupPlayerGameStates(
    gameData: GameDto | undefined, 
    currentUser: UserDto | undefined, 
    setGameState: Dispatch<SetStateAction<PlayerState | undefined>>,
    setRivalGameStates: Dispatch<SetStateAction<PlayerState[] | undefined>>
) {
    if (!gameData || !currentUser) return;
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
    // TEMP
    const boardState = [[{value: 0, notes: 0}]];
    return boardState;
}