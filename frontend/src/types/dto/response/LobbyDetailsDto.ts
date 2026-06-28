import type { Difficulty } from "@/types/enum/Difficulty"
import type { GameMode } from "@/types/enum/GameMode"
import type { GameType } from "@/types/enum/GameType"
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset"

export type LobbyDetailsDto = {
    id: number,
    lobbyName: string,
    isActive: boolean,
    inGame: boolean,
    currentGameId: number | null,
    gameType: GameType,
    gameMode: GameMode,
    difficulty: Difficulty,
    timeLimitPreset: TimeLimitPreset
}