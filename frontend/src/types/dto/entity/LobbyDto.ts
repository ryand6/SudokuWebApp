import type { Difficulty } from "../../enum/Difficulty"
import type { TimeLimitPreset } from "../../enum/TimeLimitPreset"
import type { LobbyPlayerDto } from "./LobbyPlayerDto"

export type LobbyDto = {
    id: number,
    lobbyName: string,
    createdAt: string,
    difficulty: Difficulty,
    timeLimit: TimeLimitPreset,
    isActive: boolean,
    isPublic: boolean,
    inGame: boolean,
    countdownActive: boolean,
    countdownEndsAt: string,
    countdownInitiatedBy: number,
    settingsLocked: boolean,
    lobbyPlayers: LobbyPlayerDto[],
    hostId: number
}