import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { useGetCurrentUser } from "../hooks/users/useGetCurrentUser";
import { handleUserFetchError } from "../errors/handleUserFetchError";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";

export function NewUserOnly({ children }: { children : React.ReactNode }) {
    const navigate = useNavigate();
    const location = useLocation();

    // If firstTimeSetup flag is passed to the route via navigate, use it to prevent toast notifications from being sent when a first time user sets up their account
    const firstTimeSetup = location.state?.firstTimeSetup ?? false;

    // check if the current route is the user setup page - pass flag to handleUserFetchError to prevent infinite redirects to user setup
    const onUserSetupRoute = location.pathname === "/user-setup" ? true : false;

    // Retrieve use auth status on mount - triggers currentUser to run which will redirect user to login page if not authenticated
    const { data: user, error, isLoading } = useGetCurrentUser();

    // Handle any redirects that are required
    if (error) {
        handleUserFetchError(error, navigate, location.pathname + location.search + location.hash, onUserSetupRoute);
    }

    // Once component has rendered, checks to see if user exists and if so, displays a toast notification whilst redirecting to dashboard
    useEffect(() => {
        if (!isLoading && user && !firstTimeSetup) {
            navigate("/dashboard", { replace: true });
            toast.info("Your account is already set up. Redirected to the dashboard.");
        }
    }, [isLoading, user, navigate]);

    // Dynamically show loading status - conditional re-checked when state updates
    if (isLoading) return <SpinnerButton />;

    // Redirect to referrer page, or to homepage if no referrer exists
    if (user) return null;

    return <>{children}</>;
}