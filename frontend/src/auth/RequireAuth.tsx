import { useLocation, useNavigate } from "react-router-dom";
import { handleUserFetchError } from "../utils/handleUserFetchError";
import { useCurrentUser } from "../hooks/useCurrentUser";

export function RequireAuth({ children }: { children : React.ReactNode }) {
    const navigate = useNavigate();
    const location = useLocation();

     // Retrieve use auth status on mount - triggers currentUser to run which will redirect user to login page if not authenticated
    const { data: user, error, isLoading } = useCurrentUser();

    // Handle any redirects that are required
    if (error) {
        handleUserFetchError(error, navigate, location.pathname + location.search + location.hash);
    }

    // Dynamically show loading status - conditional re-checked when state updates
    if (isLoading) return <div>Loading...</div>;

    // Update to handle - render Error page
    if (!user) return <div>Error fetching user</div>;

    return <>{children}</>;

}