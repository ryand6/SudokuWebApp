import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";

export async function beginProviderLink(providerName: string): Promise<void> {
    const response = await fetch("/api/users/begin-provider-link", {
        method: "POST",
        credentials: "include",
        headers: { 
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({providerName})
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        const error = new Error(errorData?.errorMessage || `HTTP ${response.status}`);
        throw error;
    };
}