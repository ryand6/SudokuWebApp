import type { GameDto } from "@/types/dto/entity/GameDto";

export async function checkIfUserInGame(gameId: number, userId: number): Promise<GameDto> {
    const response = await fetch(`/api/game/check-user-in-game?gameId=${gameId}&userId=${userId}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept" : "application/json" },
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Unable to verify if user is an active player in game");
    }
    return await response.json();
}