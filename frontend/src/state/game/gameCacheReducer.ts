import type { GamePlayers, PublicGameState } from "@/types/game/GameTypes";
import type { GameEvent } from "./gameEvents";
import { getCellIndex } from "@/utils/game/cellUtils";
import { normaliseGamePlayer } from "@/utils/game/normaliseGameState";
import type { GameResult } from "@/types/enum/GameResult";

export function gameCacheReducer(
    existingData: PublicGameState,
    event: GameEvent
): PublicGameState {
    switch (event.type) {
        case "PLAYER_CELL_UPDATE_ACCEPTED": {
            const cellIndex: number = getCellIndex(event.row, event.col);

            const updatedBoardProgress = [...existingData.players[event.userId].boardProgress];
            updatedBoardProgress[cellIndex] = "WON";

            return {
                ...existingData,
                players: {
                    ...existingData.players,
                    [event.userId]: {
                        ...existingData.players[event.userId],
                        score: event.score,
                        firsts: event.firsts,
                        maxStreak: event.maxStreak,
                        boardProgress: updatedBoardProgress
                    }
                },
                sharedGameState: {
                    ...existingData.sharedGameState,
                    ...(event.firstUserId !== null && { 
                        cellFirstOwnership: {
                            ...existingData.sharedGameState.cellFirstOwnership,
                            [cellIndex]: event.firstUserId
                        } 
                    })
                },
                ...(event.gameEndsAt !== null && { gameEndsAt: event.gameEndsAt })
            };
        }
        case "PLAYER_CELL_UPDATE_REJECTED": {
            return {
                ...existingData,
                players: {
                    ...existingData.players,
                    [event.userId]: {
                        ...existingData.players[event.userId],
                        score: event.score,
                        mistakes: event.mistakes
                    }
                },
                gameEndsAt: event.gameEndsAt
            };
        }
        case "PLAYER_FORFEIT": {
            return {
                ...existingData,
                players: {
                    ...existingData.players,
                    [event.gamePlayer.user.id]: normaliseGamePlayer(event.gamePlayer)
                }
            }
        }
        case "PLAYER_FINISHED": {
            return {
                ...existingData,
                players: {
                    ...existingData.players,
                    [event.gamePlayer.user.id]: normaliseGamePlayer(event.gamePlayer)
                }
            }
        }
        case "GAME_ENDED_PREMATURELY": {
            return {
                ...existingData,
                endedPrematurely: true
            }
        }
        case "GAME_RESULTS_DETERMINED": {
            const gamePlayers: GamePlayers = { ...existingData.players };
            Object.entries(event.gameResults).forEach(([userId, gameResult]: [string, GameResult]) => {
                gamePlayers[Number(userId)] = {
                    ...gamePlayers[Number(userId)],
                    gameResult
                };
            });
            return {
                ...existingData,
                players: {...gamePlayers}
            }
        }
        case "GAME_STATUS_UPDATE": {
            return {
                ...existingData,
                gameStatus: event.gameStatus
            }
        }
        case "GAME_END_TIMER_UPDATE": {
            return {
                ...existingData,
                gameEndsAt: event.gameEndsAt
            }
        }
        case "GAME_CLOCKS_INITIALISED": {
            return {
                ...existingData,
                gameStartsAt: event.gameStartsAt,
                gameEndsAt: event.gameEndsAt,
                gameStatus: event.gameStatus
            }
        }
        case "GAME_FINISHED": {
            return {
                ...existingData,
                gameEndedAt: event.gameEndedAt,
                gameStatus: event.gameStatus
            }
        }
        default: 
            return existingData;
    }
}