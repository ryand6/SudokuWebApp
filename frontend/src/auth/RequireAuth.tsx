import { useLocation, useNavigate } from "react-router-dom";
import { handleUserFetchError } from "../errors/handleUserFetchError";
import { useGetCurrentUser } from "../hooks/users/useGetCurrentUser";
import { useEffect } from "react";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";

export function RequireAuth({ children }: { children : React.ReactNode }) {
    const navigate = useNavigate();
    const location = useLocation();

     // Retrieve use auth status on mount - triggers currentUser to run which will redirect user to login page if not authenticated
    const { data: user, error, isLoading } = useGetCurrentUser();

    // Handle any redirects that are required - using effect so that this happens after RequireAuth render to satisfy requirement of calling navigate once the component has rendered
    useEffect(() => {
        if (!error) return;
        handleUserFetchError(error, navigate, location.pathname + location.search + location.hash);
    }, [error, navigate, location.pathname, location.search, location.hash]);

    // Dynamically show loading status - conditional re-checked when state updates
    if (isLoading) return <SpinnerButton />;

    // Update to handle - render Error page
    if (!user) return <div>Error fetching user</div>;

    return <>{children}</>;

}