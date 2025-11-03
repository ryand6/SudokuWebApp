import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function sendLobbyChatMessage(lobbyId: number, userId: number, username: string, message: string) {
    const response = await fetch("/api/lobby/chat/submit-chat-message", {
        method: "POST",
        credentials: "include",
        headers: { 
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({lobbyId, userId, username, message})
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.errorMessage || "Failed to send lobby chat message")
    }
    return await response.json();
}