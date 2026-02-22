import type { GameDto } from "@/types/dto/entity/GameDto";
import type { GameDtoRaw } from "@/types/dto/entity/GameDtoRaw";
import { mapGameState } from "@/utils/game/mapGameState";

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
    let rawData: GameDtoRaw = await response.json();
    // Convert base64 encoded notes representation to byte array
    return {
        ...rawData,
        gameStates: rawData.gameStates.map(mapGameState),
    }
}