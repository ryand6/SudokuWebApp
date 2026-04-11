import type { GetGameEventsResponseDto } from "@/types/dto/response/GetGameEventsDto";

export async function getGameEvents(gameId: number, pageNumber: number): Promise<GetGameEventsResponseDto> {
    const response = await fetch(`/api/game/events/get-game-events?gameId=${gameId}&page=${pageNumber}`, {
        method: "GET",
        credentials: "include",
        headers: { "Accept" : "application/json" },
    });
    return await response.json();
}