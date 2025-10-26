import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function updateLobbyTimeLimit(lobbyId: number, timeLimit: TimeLimitPreset): Promise<void> {
    try {
        const response = fetch("/api/lobby/update-time-limit", {
            method: "POST",
            credentials: "include",
            headers: { 
                // Send data in JSON format
                "Content-Type": "application/json",
                // assign token to empty string if it is null because header cannot accept null/undefined values
                "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
            },
            body: JSON.stringify({lobbyId, timeLimit})
        })
    } catch (err: any) {
        throw err;
    }
}