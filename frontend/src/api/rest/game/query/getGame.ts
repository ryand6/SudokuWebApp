import type { GameDtoRaw } from "@/types/dto/entity/GameDtoRaw";

export async function getGame(gameId: number): Promise<GameDtoRaw> {
    const response = await fetch(`/api/game/get-game?gameId=${gameId}`, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json"
        }
    });
    if (response.status === 404) throw new Error(`Game with ID ${gameId} does not exist`);
    return await response.json();
}