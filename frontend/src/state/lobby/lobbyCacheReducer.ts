import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { LobbyEvent } from "./lobbyEvents";

export function lobbyCacheReducer(
    existingData: LobbyDto,
    event: LobbyEvent
): LobbyDto {
    switch (event.type) {
        case "LOBBY_DIFFICULTY_UPDATED": {
            return {
                ...existingData,
                lobbySettings: {
                    ...existingData.lobbySettings,
                    difficulty: event.difficulty
                }
            };
        }
        case "LOBBY_TIME_LIMIT_UPDATED": {
            return {
                ...existingData,
                lobbySettings: {
                    ...existingData.lobbySettings,
                    timeLimit: event.timeLimit
                }
            };
        }
        case "LOBBY_PLAYER_STATUS_UPDATED": {
            return {
                ...existingData,
                lobbyPlayers: existingData.lobbyPlayers.map((lp) => 
                    lp.user.id === event.userId 
                    ? {
                        ...lp,
                        lobbyStatus: event.lobbyStatus
                    } : lp
                )
            };
        }
        case "LOBBY_PLAYER_JOINED": {
            return {
                ...existingData,
                lobbyPlayers: [...existingData.lobbyPlayers, event.lobbyPlayer]
            };
        }
        case "LOBBY_PLAYER_LEFT": {
            return {
                ...existingData,
                lobbyPlayers: existingData.lobbyPlayers.filter((lp) => lp.user.id !== event.userId)
            };
        }
        case "LOBBY_GAME_CREATED": {
            return {
                ...existingData,
                currentGameId: event.gameId,
                inGame: event.inGame
            };
        }
        case "GAME_ENDED": {
            return {
                ...existingData,
                inGame: false,
                currentGameId: null
            }
        }
        default:
            return existingData;
    }
}