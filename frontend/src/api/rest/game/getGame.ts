import type { GameDto } from "@/types/dto/entity/GameDto";
import type { GameDtoRaw } from "@/types/dto/entity/GameDtoRaw";
import { mapGameState } from "@/utils/game/mapGameState";

export async function getGame(gameId: number): Promise<GameDto> {
    const response = await fetch(`/api/game/get-game?gameId=${gameId}`, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json"
        }
    });
    if (response.status === 404) throw new Error(`Game with ID ${gameId} does not exist`);
    let rawData: GameDtoRaw = await response.json();
    // Convert base64 encoded notes representation to byte array
    return {
        ...rawData,
        gameStates: rawData.gameStates.map(mapGameState),
    }
}