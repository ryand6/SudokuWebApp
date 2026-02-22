import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { Difficulty } from "@/types/enum/Difficulty";
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function updateLobbyDifficulty(lobbyId: number, userId: number, difficulty: Difficulty): Promise<LobbyDto> {
    const response = await fetch("/api/lobby/update-difficulty", {
        method: "POST",
        credentials: "include",
        headers: { 
            // Send data in JSON format
            "Content-Type": "application/json",
            // assign token to empty string if it is null because header cannot accept null/undefined values
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({lobbyId, userId, difficulty})
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to update lobby difficulty setting");
    }
    return await response.json();
}