import type { LobbyPlayerDto } from "./LobbyPlayerDto"
import type { UserDto } from "../user/UserDto"
import type { LobbySettingsDto } from "./LobbySettingsDto"
import type { LobbyCountdownDto } from "./LobbyCountdownDto"

export type LobbyDto = {
    id: number,
    lobbyName: string,
    createdAt: string,
    isActive: boolean,
    inGame: boolean,
    currentGameId: number | null,
    lobbySettings: LobbySettingsDto,
    lobbyCountdown: LobbyCountdownDto,
    lobbyPlayers: LobbyPlayerDto[],
    host: UserDto
}