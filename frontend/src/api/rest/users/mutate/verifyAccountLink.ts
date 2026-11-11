import { apiFetch } from "@/api/apiFetch";
import { getCsrfTokenFromCookie } from "../../../../utils/auth/csrf";
import type { ErrorWithStatus } from "@/interfaces/ErrorWithStatus";

export async function verifyAccountLink(otp: string): Promise<void> {
    const response = await apiFetch("/api/users/verify-account-link", {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
        },
        body: JSON.stringify({ otp })
    });
    if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        const error: ErrorWithStatus = new Error(errorData?.errorMessage ?? `HTTP ${response.status}`);
        error.status = response.status;
        throw error;
    }
}