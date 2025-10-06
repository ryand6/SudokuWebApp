import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { getCsrfTokenFromCookie } from "@/utils/csrf";

export async function joinPrivateLobbyWithRequestToken(token: string): Promise<LobbyDto> {
    const response = await fetch("/api/lobby/join/private", {
        method: "POST",
        credentials: "include",
        headers: { 
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({token})
    });
    return response.json();
}