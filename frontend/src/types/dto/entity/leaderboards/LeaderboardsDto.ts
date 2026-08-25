import type { GameMode } from "@/types/enum/GameMode"

export type LeaderboardsDto = {
    userId: number,
    username: string,
    gameMode: GameMode,
    totalScore: number,
    gamesPlayed: number,
    wins: number,
    losses: number,
    draws: number,
    currentWinStreak: number,
    maxWinStreak: number
}