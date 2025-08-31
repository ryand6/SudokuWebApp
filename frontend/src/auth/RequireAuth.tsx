import { useEffect } from "react";
import { getAuthContext } from "./AuthContextProvider";

export function RequireAuth({ children }: { children : React.ReactNode }) {
    const { user, loadingUser, refreshUserAuth } = getAuthContext();

    // Retrieve use auth status on mount - triggers currentUser to run which will redirect user to login page if not authenticated
    useEffect(() => {
        refreshUserAuth();
    }, []);

    // Dynamically show loading status - conditional re-checked when state updates
    if (loadingUser) return <div>Loading...</div>;

    // Update to handle - render Error page
    if (!user) return <div>Error fetching user</div>;

    return <>{children}</>;

}