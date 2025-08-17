import type { Difficulty } from "../enum/Difficulty"
import type { TimeLimitPreset } from "../enum/TimeLimitPreset"
import type { LobbyPlayerDto } from "./LobbyPlayerDto"

export type LobbyDto = {
    id: number,
    lobbyName: string,
    difficulty: Difficulty | null,
    timeLimit: TimeLimitPreset,
    isActive: boolean,
    isPublic: boolean,
    countdownActive: boolean,
    countdownEndsAt: string,
    countdownInitiatedBy: number,
    settingsLocked: boolean,
    lobbyPlayers: LobbyPlayerDto[],
    hostId: number
}