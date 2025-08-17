export async function getCurrentUser() {
    const response = await fetch("/api/users/current-user", {
        credentials: "include"
    });
    if (!response.ok) return null;
    return await response.json();
}