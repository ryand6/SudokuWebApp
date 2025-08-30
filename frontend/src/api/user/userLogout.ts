import { getCsrfToken } from "../../utils/csrf";

export async function userLogout() {
    try {
        const response = await fetch("/logout", {
            method: "POST",
            credentials: "include",
            headers: {
                // expect json data to be returned
                "Accept": "application/json",
                // assign token to empty string if it is null because header cannot accept null/undefined values
                "X-XSRF-TOKEN": getCsrfToken() ?? "",
            }
        });
        if (!response.ok) {
            // if error message doesn't parse properly, assign null to errorData
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || `HTTP ${response.status}`);
        }
    } catch (err: any) {
        throw new Error(err?.message || "Logout failed");
    }
}