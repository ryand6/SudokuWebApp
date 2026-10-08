export async function getActiveGameId(): Promise<number | null> {
    const response = await fetch("/api/users/get-active-game-id", {
        method: "GET",
        headers: { "Accept": "application/json" },
    });
    return await response.json();
}