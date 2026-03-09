import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function leaveGame(gameId: number) {
    const response = fetch(`/api/game/leave`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({gameId})
    });
}