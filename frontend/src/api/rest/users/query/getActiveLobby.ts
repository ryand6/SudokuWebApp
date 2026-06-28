import type { LobbyDetailsDto } from "@/types/dto/response/LobbyDetailsDto";

export async function getActiveLobby(): Promise<LobbyDetailsDto> {
    const response = await fetch("/api/users/get-active-lobby", {
        method: "GET",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}