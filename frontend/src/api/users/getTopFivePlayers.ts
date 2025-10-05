import type { TopFivePlayersDto } from "../../types/dto/response/TopFivePlayersDto";

export async function getTopFivePlayers(): Promise<TopFivePlayersDto> {
    const response = await fetch("/api/users/get-top-five-players", {
        method: "GET",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}