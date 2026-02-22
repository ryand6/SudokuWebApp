import { backendValidationErrors } from "../../../../utils/error/backendValidationErrors";
import { getCsrfTokenFromCookie } from "../../../../utils/auth/csrf";
import type { ErrorWithStatus } from "@/interfaces/ErrorWithStatus";

export async function processUserSetup(username: string): Promise<void> {
    try {
        const response = await fetch("/api/users/process-user-setup", {
            method: "POST",
            credentials: "include",
            headers: { 
                // Send data in JSON format
                "Content-Type": "application/json",
                // assign token to empty string if it is null because header cannot accept null/undefined values
                "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
            },
            body: JSON.stringify({username})
        });
        if (!response.ok) {
            // if error message doesn't parse properly, assign null to errorData
            const errorData = await response.json().catch(() => null);
            let error: ErrorWithStatus;
            if (Array.isArray(errorData)) {
                error = backendValidationErrors(errorData);
            } else {
                error = new Error(errorData?.errorMessage || `HTTP ${response.status}`);
            }
            error.status = response.status;
            throw error;
        };
    } catch (err: any) {
        throw err;
    }
}