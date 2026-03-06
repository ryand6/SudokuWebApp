import type { PrivateGamePlayerStateDtoRaw } from "@/types/dto/entity/game/PrivateGamePlayerStateDtoRaw";

export async function getGamePlayerState(gameId: number): Promise<PrivateGamePlayerStateDtoRaw> {
    const response = await fetch(`/api/game/get-game-player-state?gameId=${gameId}`, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json"
        }
    });
    if (response.status === 404) throw new Error(`Game player state not found`);
    return await response.json();
}