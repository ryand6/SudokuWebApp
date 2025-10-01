import type { LobbyPlayerId } from "./LobbyPlayerId"
import type { UserDto } from "./UserDto"
import type { LobbyStatus } from "../../enum/LobbyStatus"
import type { PreferenceDirection } from "../../enum/PreferenceDirection"

export type LobbyPlayerDto = {
    id: LobbyPlayerId,
    user: UserDto,
    joinedAt: string,
    lobbyStatus: LobbyStatus,
    readyAt: string | null,
    difficultyPreference: PreferenceDirection,
    difficultyVoteTimestamp: string | null,
    durationPreference: PreferenceDirection,
    durationVoteTimestamp: string | null
}