import type { GamePlayerSettingsDto } from "./GamePlayerSettingsDto"

export type PrivateGamePlayerStateDtoRaw = {
    currentBoardState: string,
    // Base64 encoded representation
    notes: string,
    currentStreak: number,
    activeMultiplier: number,
    multiplierEndsAt: string | null,
    gamePlayerSettings: GamePlayerSettingsDto
}