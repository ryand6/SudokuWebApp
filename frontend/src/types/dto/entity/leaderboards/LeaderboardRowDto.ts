export type LeaderboardRowDto = {
    rank: number,
    userId: number,
    username: string,
    totalScore: number,
    gamesPlayed: number,
    wins: number,
    losses: number,
    draws: number,
    maxWinStreak: number
}