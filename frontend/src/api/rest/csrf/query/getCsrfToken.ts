import type { CsrfResponseData } from "@/types/dto/auth/CsrfResponseData";

// Retrieves xor encoded csrf token from backend, for use in requests that require this such as websocket connections
export async function getCsrfToken(): Promise<CsrfResponseData | null> {
    const response = await fetch("/csrf", { 
        credentials: "include",
        redirect: "manual"
    });
    if (response.type === "opaqueredirect" || response.status === 401) {
        return null;
    }
    if (!response.ok) {
        throw new Error("Error fetching csrf token");
    }
    const data = await response.json();
    return data;
}