import type { LobbyStatus } from "@/types/enum/LobbyStatus";
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function updateLobbyPlayerStatus(lobbyId: number, userId: number, lobbyStatus: LobbyStatus) {
    const response = await fetch("/api/lobby/update-player-status", {
        method: "POST",
        credentials: "include",
        headers: { 
            // Send data in JSON format
            "Content-Type": "application/json",
            // assign token to empty string if it is null because header cannot accept null/undefined values
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({lobbyId, userId, lobbyStatus}),
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to update lobby player status");
    }
    return await response.json();
}