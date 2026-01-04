import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function updateLobbyTimeLimit(lobbyId: number, userId: number, timeLimit: TimeLimitPreset): Promise<LobbyDto> {
    const response = await fetch("/api/lobby/update-time-limit", {
        method: "POST",
        credentials: "include",
        headers: { 
            // Send data in JSON format
            "Content-Type": "application/json",
            // assign token to empty string if it is null because header cannot accept null/undefined values
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({lobbyId, userId, timeLimit})
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to update lobby time limit setting");
    }
    return await response.json();
}