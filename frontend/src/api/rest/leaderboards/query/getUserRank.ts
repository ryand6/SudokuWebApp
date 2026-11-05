import { apiFetch } from "@/api/apiFetch";
import type { GameMode } from "@/types/enum/GameMode";

export async function getUserRank(gameMode: GameMode): Promise<number> {
    const response = await apiFetch(`/api/leaderboards/get-user-rank?gameMode=${gameMode}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}