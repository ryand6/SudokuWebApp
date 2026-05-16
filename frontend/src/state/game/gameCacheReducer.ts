import type { PublicGameState } from "@/types/game/GameTypes";
import type { GameEvent } from "./gameEvents";
import { getCellIndex } from "@/utils/game/cellUtils";
import { normaliseGamePlayer } from "@/utils/game/normaliseGameState";

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
        case "GAME_STATUS_UPDATE": {
            // IMPLEMENT
            return existingData;
        }
        case "GAME_TIMER_UPDATE": {
            // IMPLEMENT - gameStartsAt and gameEndsAt ?
            return existingData;
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
        default: 
            return existingData;
    }
}