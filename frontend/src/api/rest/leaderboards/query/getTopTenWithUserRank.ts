import type { TopTenLeaderboardRow } from "@/types/dto/response/TopTenWithUserRankDto";
import type { GameMode } from "@/types/enum/GameMode";

export async function getTopTenWithUserRank(gameMode: GameMode): Promise<TopTenLeaderboardRow[]> {
    const response = await fetch(`/api/leaderboards/get-top-ten-with-user-rank?gameMode=${gameMode}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}