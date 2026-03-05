import type { Difficulty } from "@/types/enum/Difficulty"
import type { GameMode } from "@/types/enum/GameMode"
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset"

export type LobbySettingsDto = {
    isPublic: boolean,
    difficulty: Difficulty,
    timeLimit: TimeLimitPreset,
    gameMode: GameMode
}