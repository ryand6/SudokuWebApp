import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function joinPublicLobby(lobbyId: number): Promise<LobbyDto> {
    const response = await fetch(`/api/lobby/join/public/${lobbyId}`, {
        method: "POST",
        credentials: "include",
        headers: { 
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        }
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to join public lobby")
    }
    return await response.json();
}