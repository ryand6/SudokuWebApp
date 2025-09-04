import { useEffect } from "react";
import { getAuthContext } from "./AuthContextProvider";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function NewUserOnly({ children }: { children : React.ReactNode }) {
    const { user, loadingUser, refreshUserAuth } = getAuthContext();

    const navigate = useNavigate();

    // Retrieve use auth status on mount - triggers currentUser to run which will redirect user to login page if not authenticated
    useEffect(() => {
        refreshUserAuth();
    }, []);

    // Once component has rendered, checks to see if user exists and if so, displays a toast notification whilst redirecting to dashboard
    useEffect(() => {
        if (!loadingUser && user) {
            navigate("/dashboard", { replace: true });
            toast.info("Your account is already set up. Redirected to the dashboard.");
        }
    }, [loadingUser, user, navigate]);

    // Dynamically show loading status - conditional re-checked when state updates
    if (loadingUser) return <div>Loading...</div>;

    // Redirect to referrer page, or to homepage if no referrer exists
    if (user) return null;

    return <>{children}</>;

}