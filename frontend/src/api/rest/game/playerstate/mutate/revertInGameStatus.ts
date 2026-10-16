import type { ErrorWithStatus } from "@/interfaces/ErrorWithStatus";
import type { RevertInGameStatusResponseDto } from "@/types/dto/response/RevertInGameStatusResponseDto";
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function revertInGameStatus(lobbyId: number): Promise<RevertInGameStatusResponseDto> {
    const response = await fetch(`/api/game-player-state/revert-in-game-status`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({lobbyId})
    });
    if (!response.ok) {
        // if error message doesn't parse properly, assign null to errorData
        const errorData = await response.json().catch(() => null);
        const error: ErrorWithStatus = new Error(errorData?.errorMessage ?? `HTTP ${response.status}`);
        error.status = response.status;
        throw error;
    };
    return response.json();

}