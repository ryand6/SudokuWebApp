import type { GameHighlightedCellsResponseDto } from "@/types/dto/response/GameHighlightedCellsResponseDto";

export async function getGameHighlightedCells(gameId: number): Promise<GameHighlightedCellsResponseDto> {
    const response = await fetch(`/api/game/memory/get-game-highlighted-cells?gameId=${gameId}`, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json"
        }
    });
    return await response.json();
}