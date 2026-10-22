import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";
import type { ErrorWithStatus } from "@/interfaces/ErrorWithStatus";
import type { GameDto } from "@/types/dto/entity/game/GameDto";

export async function onContinue(gameId: number): Promise<GameDto> {
    const response = await fetch(`/api/game/on-continue`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({gameId})
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