import type { NavigateFunction } from "react-router-dom";

export function redirectPostLogin(navigate: NavigateFunction) {
    // Using session storage so that there isn't any overwriting of postLoginPath from different tabs - stores one per tab
    const referrer = sessionStorage.getItem("postLoginPath");
    // No redirect URL saved therefore stop
    if (!referrer) return;
    // Check for potential harmful redirect URLs - referrer URLs should only be local page paths
    if (!referrer.startsWith("/")) {
        localStorage.removeItem("postLoginPath"); 
        return;
    }
    // Don't bounce back to login/account setup flows
    const pathnameOnly = referrer.split("?")[0];
    if (pathnameOnly === "/login" || pathnameOnly === "/user-setup") {
        localStorage.removeItem("postLoginPath");
        return;
    }
    sessionStorage.removeItem("postLoginPath");
    navigate(referrer, { replace: true });
}