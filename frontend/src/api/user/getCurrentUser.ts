import type { UserDto } from "../../types/dto/UserDto";


export async function getCurrentUser(): Promise<UserDto> {
    const response = await fetch("/api/users/current-user", {
        method: "GET",
        credentials: "include",
        // expect json data to be returned
        headers: { "Accept": "application/json" },
        // returns type: opaqueredirect, status: 0
        redirect: "manual",
    });
    // Handle either if Spring Boot has detected that an unauthorised user has tried accessing a protected backend endpoint (opaqueredirect), or a OAuth2LoginRequiredException has been caught (401)
    if (response.type === "opaqueredirect" || response.status === 401) {
        throw new Error("RedirectToLogin");    
    // Handle when a user has not been created
    } else if (response.status === 404) {
        throw new Error("UserSetupRequired");
    } else if (!response.ok) {
        // if error message doesn't parse properly, assign null to errorData
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || `HTTP ${response.status}`);
    };
    return await response.json();
}