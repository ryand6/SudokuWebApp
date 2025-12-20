import type { LobbyDto } from "@/types/dto/entity/LobbyDto";

export async function getLobby(lobbyId: number): Promise<LobbyDto> {
    const response = await fetch(`/api/lobby/get-lobby?lobbyId=${lobbyId}`, {
        method: "GET",
        credentials: "include",
        headers: {
            "Accept": "application/json"
        }
    });
    if (response.status === 404) throw new Error(`Lobby with ID ${lobbyId} does not exist`);
    return await response.json();
}