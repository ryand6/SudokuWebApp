export type GamePlayerStateDtoRaw = {
    currentBoardState: string,
    // Base64 encoded representation
    notes: string,
    currentStreak: number,
    activeMultiplier: number,
    multiplierEndsAt: string | null
}