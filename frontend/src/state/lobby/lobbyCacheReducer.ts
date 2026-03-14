import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { LobbyEvent } from "./lobbyEvents";

export function lobbyCacheReducer(
    existingData: LobbyDto | undefined,
    event: LobbyEvent
): LobbyDto {
    switch (event.type) {
        case "LOBBY_UPDATED": {
            return event.lobbyData;
        }
    }
}