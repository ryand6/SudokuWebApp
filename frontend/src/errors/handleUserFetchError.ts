import type { NavigateFunction } from "react-router-dom";

export function handleUserFetchError(
      error: Error,
      navigate: NavigateFunction,
      currentPath: string,
      onUserSetupRoute?: boolean
) {
    if (!error) return;
    // Redirect to login
    if (error.message === "RedirectToLogin") {
        sessionStorage.setItem("postLoginPath", currentPath);
        window.location.replace("http://localhost:8080/login");
        return;
    }
    // Redirect to user setup - prevent redirection if the check is being performed inside the UserSetupPage route to prevent infinite looping
    else if (error.message === "UserSetupRequired" && !onUserSetupRoute) {
        sessionStorage.setItem("postLoginPath", currentPath);
        navigate("/user-setup", { replace: true, state: { firstTimeSetup: true } });
        return;
    } 
    else if (onUserSetupRoute) return;
    // For other errors, you could log or handle differently
    console.error("Unhandled user fetch error:", error);
}