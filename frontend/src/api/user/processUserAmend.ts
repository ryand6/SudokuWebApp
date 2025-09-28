import { backendValidationErrors } from "../../utils/backendValidationErrors";
import { getCsrfTokenFromCookie } from "../../utils/csrf";

export async function processUserAmend(username: string): Promise<void> {
    try {
        const response = await fetch("/api/users/process-user-amend", {
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
            let error: any;
            if (Array.isArray(errorData)) {
                error = backendValidationErrors(errorData);
            } else {
                error = new Error(errorData?.message || `HTTP ${response.status}`);
            }
            error.status = response.status;
            throw error;
        };
    } catch (err: any) {
        throw err;
    }
}