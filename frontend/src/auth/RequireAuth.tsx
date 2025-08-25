import { useContext, useEffect, type JSX } from "react";
import { getAuthContext } from "./AuthContextProvider";
import { Navigate, useLocation } from "react-router-dom";

export function RequireAuth({ children }: { children : React.ReactNode }): JSX.Element | null {

    const { user, loadingUser, userSetupRequired, loginRequired, refreshUser } = getAuthContext();
    const location = useLocation();

    // Retrieve use auth status on mount
    useEffect(() => {
        refreshUser();
    }, []);

    // Dynamically show loading status - conditional re-checked when state updates
    if (loadingUser) return <div>Loading...</div>;

    if (loginRequired) {
        // Store the SPA route the user originally tried accessing so they can be redirected post login
        sessionStorage.setItem("postLoginPath", location.pathname + location.search + location.hash);
        return <Navigate to="/login" replace={true} />;
    }

    // If user account does not exist, redirect to setup page to set their account up - provide referrer URL so they can return to original intended URL
    if (userSetupRequired) {
        // Store the SPA route the user originally tried accessing so they can be redirected post account set up
        sessionStorage.setItem("postLoginPath", location.pathname + location.search + location.hash);
        return <Navigate to="/user-setup" replace={true} />;
    }

    // Update to handle - render Error page
    if (!user) return <div>Error fetching user</div>;

    return <>{children}</>;

}