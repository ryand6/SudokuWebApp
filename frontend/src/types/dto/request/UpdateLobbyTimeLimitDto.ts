import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset"

export type UpdateLobbyTimeLimitDto = {
    lobbyId: number,
    userId: number,
    timeLimit: TimeLimitPreset
}