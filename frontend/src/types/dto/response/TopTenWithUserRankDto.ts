type TopTenLeaderboardRow = {
    rank: number,
    userId: number,
    username: string,
    total_score: number
}

export type TopTenWithUserRankDto = {
    leaderboardRows: TopTenLeaderboardRow[]
}