import { useEffect, useRef, type JSX } from "react";
import { getAuthContext } from "./AuthContextProvider";
import { useNavigate } from "react-router-dom";

export function PostLoginRedirectHandler({ children }: { children: React.ReactNode }): JSX.Element {
    const { user } = getAuthContext();
    const navigate = useNavigate();
    // Used to keep track per mount whether the postLoginRedirect occurred, so that no more redirects accidentally occur - guard
    const didRedirectOccur = useRef(false);
    
    // Redirect users after login or account setup is complete
    useEffect(() => {
        // Redirect should only occur once user has logged in/created account
        if (!user) return;
        // Prevent multiple post login redirects occurring per mount accidentally - should only redirect once
        if (didRedirectOccur.current) return; 
        // Using session storage so that there isn't any overwriting of postLoginPath from different tabs - stores one per tab
        const referrer = sessionStorage.getItem("postLoginPath");
        // No redirect URL saved therefore stop
        if (!referrer) return;
        // Check for potential hamrful redirect URLs - referrer URLs should only be local page paths
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
        // Mark redirect having occurred so that it can't occur again until a new mount
        didRedirectOccur.current = true;
        sessionStorage.removeItem("postLoginPath");
        navigate(referrer, { replace: true });
    }, [user]);

    return <>{children}</>;

}