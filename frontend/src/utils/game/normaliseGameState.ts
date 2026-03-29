import { CELL_COUNT, GRID_SIZE } from "./gameConstants";
import type { PrivateCellState, PublicGameState, GamePlayers, PrivateGamePlayerState, SharedGameState, SharedCellState } from "@/types/game/GameTypes";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { PrivateGamePlayerStateDtoRaw } from "@/types/dto/entity/game/PrivateGamePlayerStateDtoRaw";
import { mapGameState } from "./mapGameState";
import type { PrivateGamePlayerStateDto } from "@/types/dto/entity/game/PrivateGamePlayerStateDto";
import type { SharedGameStateDto } from "@/types/dto/entity/game/SharedGameStateDto";

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
            firsts: gp.firsts,
            mistakes: gp.mistakes,
            gameLoaded: gp.gameLoaded,
            gameResult: gp.gameResult
        };
    });
    playerIds.sort();
    const gameState: PublicGameState = {
        gameId: gameData.gameId,
        playerIds: playerIds,
        players: players,
        sharedGameState: normaliseSharedGameStateData(gameData.sharedGameState),
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

function normaliseSharedGameStateData(
    sharedGameStateDto: SharedGameStateDto
): SharedGameState {
    return {
        cellFirstOwnership: sharedGameStateDto.cellFirstOwnership,
        currentSharedBoardState: sharedGameStateDto.currentSharedBoardState != null 
            ? fillPublicBoardState(sharedGameStateDto.currentSharedBoardState)
            : undefined
    }
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
        boardState: fillPrivateBoardState(normalisedGameDtoState),
        currentStreak: normalisedGameDtoState.currentStreak,
        activeMultiplier: normalisedGameDtoState.activeMultiplier,
        multiplierEndsAt: normalisedGameDtoState.multiplierEndsAt,
        settings: normalisedGameDtoState.gamePlayerSettings
    }
}

function fillPrivateBoardState(gameDtoState: PrivateGamePlayerStateDto): PrivateCellState[][] {
    const boardState = [];
    for (let y = 0; y < GRID_SIZE; y++) {
        let boardRow: PrivateCellState[] = [];
        for (let x = 0; x < GRID_SIZE; x++) {
            const value = getCellValue(gameDtoState.currentBoardState, y, x);
            const notes = gameDtoState.notes[(y * 9) + x];
            boardRow[x] = {value: value, notes: notes, isRejected: false};
        }
        boardState.push(boardRow);
    }
    return boardState;
}

function fillPublicBoardState(currentSharedBoardState: string) {
    const boardState = [];
    for (let y = 0; y < GRID_SIZE; y++) {
        let boardRow: SharedCellState[] = [];
        for (let x = 0; x < GRID_SIZE; x++) {
            const value = getCellValue(currentSharedBoardState, y, x);
            boardRow[x] = {value: value};
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