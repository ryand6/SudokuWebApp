import type { GameHighlightedCellsResponseDto } from "@/types/dto/response/GameHighlightedCellsResponseDto";

export async function getGameHighlightedCells(gameId: number): Promise<GameHighlightedCellsResponseDto | null> {
    const response = await fetch(`/api/game/memory/get-game-highlighted-cells?gameId=${gameId}`, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json"
        }
    });
    if (response.status === 204) {
        return null;
    }
    return await response.json();
}