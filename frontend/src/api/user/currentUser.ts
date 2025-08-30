import { type Location, type NavigateFunction } from "react-router-dom";
import type { UserDto } from "../../types/dto/UserDto";


export async function getCurrentUser(
    navigate: NavigateFunction,
    location: Location
): Promise<UserDto> {
    try {
        const response = await fetch("/api/users/current-user", {
            method: "GET",
            credentials: "include",
            // expect json data to be returned
            headers: { "Accept": "application/json" },
            // returns type: opaqueredirect, status: 0
            redirect: "manual",
        });
        // Handle either if Spring Boot has detected that an unauthorised user has tried accessing a protected backend endpoint (opaqueredirect), or a OAuth2LoginRequiredException has been caught (401), redirect to login
        if (response.type === "opaqueredirect" || response.status === 401) {
            sessionStorage.setItem("postLoginPath", location.pathname + location.search + location.hash);
            window.location.replace("http://localhost:8080/login");
            throw new Error("Redirecting to login");    
        // Handle when a user has not been created, redirect to user set-up
        } else if (response.status === 404) {
            sessionStorage.setItem("postLoginPath", location.pathname + location.search + location.hash);
            navigate("/user-setup", { replace: true });
            throw new Error("User not found, redirecting to user setup");
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