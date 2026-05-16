import { CELL_COUNT, GRID_SIZE } from "./gameConstants";
import type { PrivateCellState, PublicGameState, GamePlayers, PrivateGamePlayerState, SharedGameState, SharedCellState, GamePlayer } from "@/types/game/GameTypes";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { PrivateGamePlayerStateDtoRaw } from "@/types/dto/entity/game/PrivateGamePlayerStateDtoRaw";
import { mapGameState } from "./mapGameState";
import type { PrivateGamePlayerStateDto } from "@/types/dto/entity/game/PrivateGamePlayerStateDto";
import type { SharedGameStateDto } from "@/types/dto/entity/game/SharedGameStateDto";
import type { GamePlayerDto } from "@/types/dto/entity/game/GamePlayerDto";

export function normalisePublicGameData(
    gameData: GameDto
): PublicGameState {
    const playerIds: number[] = [];
    const players: GamePlayers = {};

    gameData.gamePlayers.forEach((gp) => {
        playerIds.push(gp.user.id);
        players[gp.user.id] = normaliseGamePlayer(gp);
    });
    playerIds.sort();
    const gameState: PublicGameState = {
        gameId: gameData.gameId,
        lobbyId: gameData.lobbyId, 
        playerIds: playerIds,
        players: players,
        sharedGameState: normaliseSharedGameStateData(gameData.sharedGameState),
        initialBoardState: gameData.initialBoardState,
        gameSettings: gameData.gameSettings,
        gameStatus: gameData.gameStatus,
        gameStartsAt: gameData.gameStartsAt,
        gameEndsAt: gameData.gameEndsAt,
        endedPrematurely: gameData.endedPrematurely
    };

    return gameState;
}

export function normaliseGamePlayer(
    player: GamePlayerDto
): GamePlayer {
    return {
        name: player.user.username,
        colour: player.playerColour,
        boardProgress: player.boardProgress,
        score: player.score,
        firsts: player.firsts,
        mistakes: player.mistakes,
        maxStreak: player.maxStreak,
        gameLoaded: player.gameLoaded,
        gameResult: player.gameResult,
        finishedGame: player.finishedGame,
        finishedGameTimestamp: player.finishedGameTimestamp
    }
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
        leaderboardResult: undefined
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