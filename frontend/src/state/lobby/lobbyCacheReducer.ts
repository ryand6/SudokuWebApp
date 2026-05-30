import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { LobbyEvent } from "./lobbyEvents";

export function lobbyCacheReducer(
    existingData: LobbyDto,
    event: LobbyEvent
): LobbyDto {
    switch (event.type) {
        case "LOBBY_UPDATED": {
            return event.lobbyData;
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