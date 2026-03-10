import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";
import type { ErrorWithStatus } from "@/interfaces/ErrorWithStatus";

export async function leaveGame(gameId: number) {
    const response = await fetch(`/api/game/leave`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({gameId})
    });

    if (response.status === 204) {
        return null;
    }

    if (!response.ok) {
        // if error message doesn't parse properly, assign null to errorData
        const errorData = await response.json().catch(() => null);
        const error: ErrorWithStatus = new Error(errorData?.errorMessage ?? `HTTP ${response.status}`);
        error.status = response.status;
        throw error;
    };
    return response.json();

}