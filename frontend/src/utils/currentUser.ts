import type { UserDto } from "../dto/UserDto";

export async function getCurrentUser(): Promise<UserDto> {
    try {
        const response = await fetch("/api/users/current-user", {
            method: "GET",
            credentials: "include",
            // expect json data to be returned
            headers: { "Accept": "application/json" },
        });
        // Handle when the user has not logged in via OAuth2 yet
        if (response.status === 401) {
            throw new Error("OAuth2 login required");
        // Handle when a user has not been created
        } else if (response.status === 404) {
            throw new Error("User not found, account set up required");
        } else if (!response.ok) {
            // if error message doesn't parse properly, assign null to errorData
            const errorData = await response.json().catch(() => null);
            throw new Error(errorData?.message || `HTTP ${response.status}`);
        };
        return await response.json();
    // catch error in try block and display error message - backup error message provided in case of unexpected behaviour
    } catch (err: any) {
        throw new Error(err?.message || "Failed to fetch current user");
    }
}