import { apiFetch } from "@/api/apiFetch";
import type { LeaderboardsDto } from "@/types/dto/entity/leaderboards/LeaderboardsDto";
import type { GameMode } from "@/types/enum/GameMode";

export async function getUserGameModeStats(gameMode: GameMode): Promise<LeaderboardsDto> {
    const response = await apiFetch(`/api/leaderboards/get-stats?gameMode=${gameMode}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}