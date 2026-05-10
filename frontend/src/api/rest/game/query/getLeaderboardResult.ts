import type { LeaderboardResult } from "@/types/game/GameTypes";

export async function getLeaderboardResult(gameId: number): Promise<LeaderboardResult>  {
    const response = await fetch(`/api/game/get-leaderboard-result?gameId=${gameId}`, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json"
        }
    });
    if (response.status === 400) throw new Error(`Unable to retrieve leaderboard result.`);
    return await response.json();
}