import { apiFetch } from "@/api/apiFetch";
export async function getActiveGameId(): Promise<number | null> {
    const response = await apiFetch("/api/users/get-active-game-id", {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}