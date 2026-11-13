import { apiFetch } from "@/api/apiFetch";
import { getCsrfTokenFromCookie } from "../../../../utils/auth/csrf";
import type { ErrorWithStatus } from "@/interfaces/ErrorWithStatus";

export async function requestAccountLink(email: string): Promise<string> {
    const response = await apiFetch("/api/users/request-account-link", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({ email })
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        const error: ErrorWithStatus = new Error(errorData?.errorMessage ?? `HTTP ${response.status}`);
        error.status = response.status;
        throw error;
    }
    const data = await response.json();
    return data.message;
}