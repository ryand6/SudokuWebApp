import type { LobbyTokenResponseDto } from "@/types/dto/response/LobbyTokenResponseDto";
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function requestJoinCode(lobbyId: number, userId: number): Promise<LobbyTokenResponseDto> {
    const response = await fetch("/api/lobby/token/request-token", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({lobbyId, userId})
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to generate new private lobby join code");
    }
    return await response.json();
} 