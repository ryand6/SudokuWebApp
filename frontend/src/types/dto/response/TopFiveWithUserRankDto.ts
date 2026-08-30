export type TopFiveLeaderboardRow = {
    rank: number,
    userId: number,
    username: string,
    totalScore: number
}

export type TopFiveWithUserRankDto = {
    leaderboardRows: TopFiveLeaderboardRow[]
}