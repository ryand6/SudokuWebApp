import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { getCsrfTokenFromCookie } from "@/utils/csrf";

export async function joinPrivateLobby(token: string): Promise<LobbyDto> {
    const response = await fetch("/api/lobby/join/private", {
        method: "POST",
        credentials: "include",
        headers: { 
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({token})
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to join private lobby")
    }
    return await response.json();
}