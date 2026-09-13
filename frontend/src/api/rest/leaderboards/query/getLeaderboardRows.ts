import type { LeaderboardRowDto } from "@/types/dto/entity/leaderboards/LeaderboardRow";
import type { GameMode } from "@/types/enum/GameMode";

export async function getLeaderboardRows(gameMode: GameMode, pageNumber: number): Promise<LeaderboardRowDto[]> {
    const response = await fetch(`/api/leaderboards/get-leaderboard-rows?gameMode=${gameMode}&page=${pageNumber}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}