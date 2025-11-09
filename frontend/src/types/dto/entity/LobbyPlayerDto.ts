import type { LobbyPlayerId } from "./LobbyPlayerId"
import type { UserDto } from "./UserDto"
import type { LobbyStatus } from "../../enum/LobbyStatus"

export type LobbyPlayerDto = {
    id: LobbyPlayerId,
    user: UserDto,
    joinedAt: string,
    lobbyStatus: LobbyStatus,
    readyAt: string | null,
    lobbyMessageTimestamp: string | null,
}