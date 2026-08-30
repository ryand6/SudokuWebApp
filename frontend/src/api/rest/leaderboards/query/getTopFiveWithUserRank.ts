import type { TopFiveLeaderboardRow } from "@/types/dto/response/TopFiveWithUserRankDto";
import type { GameMode } from "@/types/enum/GameMode";

export async function getTopFiveWithUserRank(gameMode: GameMode): Promise<TopFiveLeaderboardRow[]> {
    const response = await fetch(`/api/leaderboards/get-top-five-with-user-rank?gameMode=${gameMode}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}