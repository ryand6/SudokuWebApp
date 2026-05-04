import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";

export type GameSettingsDto = {
    difficulty: Difficulty,
    timeLimit: TimeLimitPreset,
    gameMode: GameMode,
    gameType: GameType
}