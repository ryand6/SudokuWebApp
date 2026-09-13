import type { LeaderboardRowDto } from "@/types/dto/entity/leaderboards/LeaderboardRow";
import type { GameMode } from "@/types/enum/GameMode";

export async function getUserLeaderboardRow(gameMode: GameMode): Promise<LeaderboardRowDto> {
    const response = await fetch(`/api/leaderboards/get-user-leaderboard-row?gameMode=${gameMode}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}