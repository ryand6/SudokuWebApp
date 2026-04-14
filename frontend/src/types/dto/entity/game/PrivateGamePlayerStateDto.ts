export type PrivateGamePlayerStateDto = {
    currentBoardState: string | null,
    // 16 bit array representing bitmask of each cell's notes
    notes: Uint16Array,
    currentStreak: number,
    activeMultiplier: number,
    multiplierEndsAt: string | null
}