import { apiFetch } from "@/api/apiFetch";
import type { LobbyDetailsDto } from "@/types/dto/response/LobbyDetailsDto";

export async function getActiveLobby(): Promise<LobbyDetailsDto | null> {
    const response = await apiFetch("/api/users/get-active-lobby", {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}