import { CELL_COUNT, GRID_SIZE } from "./gameConstants";
import type { CellState, PublicGameState, GamePlayers, PrivateGamePlayerState } from "@/types/game/GameTypes";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { PrivateGamePlayerStateDtoRaw } from "@/types/dto/entity/game/PrivateGamePlayerStateDtoRaw";
import { mapGameState } from "./mapGameState";
import type { PrivateGamePlayerStateDto } from "@/types/dto/entity/game/PrivateGamePlayerStateDto";

export function normalisePublicGameData(
    gameData: GameDto
): PublicGameState {
    const playerIds: number[] = [];
    const players: GamePlayers = {};

    gameData.gamePlayers.forEach((gp) => {
        playerIds.push(gp.user.id);
        players[gp.user.id] = {
            name: gp.user.username,
            colour: gp.playerColour,
            boardProgress: gp.boardProgress,
            score: gp.score,
            gameLoaded: gp.gameLoaded,
            gameResult: gp.gameResult
        };
    });
    playerIds.sort();
    const gameState: PublicGameState = {
        gameId: gameData.gameId,
        playerIds: playerIds,
        players: players,
        sharedGameState: gameData.sharedGameState,
        initialBoardState: gameData.initialBoardState,
        gameMode: gameData.gameMode,
        difficulty: gameData.difficulty,
        timeLimit: gameData.timeLimit,
        gameStatus: gameData.gameStatus,
        gameStartsAt: gameData.gameStartsAt,
        gameEndsAt: gameData.gameEndsAt 
    };

    return gameState;
}

// Handles client's private game state data (not visible to opponents)
export function normalisePrivateGameStateData(
    gameState: PrivateGamePlayerStateDtoRaw
): PrivateGamePlayerState {
    if (gameState.currentBoardState.length != CELL_COUNT) {
        throw new Error(`Expected board state string to be ${CELL_COUNT} characters long. Instead got ${gameState.currentBoardState.length} characters.`)
    }
    const normalisedGameDtoState: PrivateGamePlayerStateDto = mapGameState(gameState);
    return {
        boardState: fillBoardState(normalisedGameDtoState),
        currentStreak: normalisedGameDtoState.currentStreak,
        activeMultiplier: normalisedGameDtoState.activeMultiplier,
        multiplierEndsAt: normalisedGameDtoState.multiplierEndsAt,
        settings: normalisedGameDtoState.gamePlayerSettings
    }
}

function fillBoardState(gameDtoState: PrivateGamePlayerStateDto): CellState[][] {
    const boardState = [];
    for (let y = 0; y < GRID_SIZE; y++) {
        let boardRow: CellState[] = [];
        for (let x = 0; x < GRID_SIZE; x++) {
            const value = getCellValue(gameDtoState.currentBoardState, y, x);
            const notes = gameDtoState.notes[(y * 9) + x];
            boardRow[x] = {value: value, notes: notes, isRejected: false};
        }
        boardState.push(boardRow);
    }
    return boardState;
}

function getCellValue(boardState: string | null, y: number, x: number): string | undefined {
    if (boardState === null) {
        return undefined;
    }
    // Map 2D co-ords to 1D array positions
    let value = boardState.at((y * 9) + x);
    if (value === ".") {
        value = undefined;
    }
    return value;
}