export type TopTenLeaderboardRow = {
    rank: number,
    userId: number,
    username: string,
    totalScore: number
}

export type TopTenWithUserRankDto = {
    leaderboardRows: TopTenLeaderboardRow[]
}