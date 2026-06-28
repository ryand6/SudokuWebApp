import { useLocation, useNavigate } from "react-router-dom";
import { handleUserFetchError } from "../errors/handleUserFetchError";
import { useGetCurrentUser } from "../api/rest/users/query/useGetCurrentUser";
import { useEffect } from "react";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";

export function RequireAuth({ children }: { children : React.ReactNode }) {
    const navigate = useNavigate();
    const location = useLocation();

    // check if the current route is the link account page - pass flag to handleUserFetchError to prevent infinite redirects to link account page
    const onLinkAccountRoute: boolean = location.pathname === "/link-account";

     // Retrieve use auth status on mount - triggers currentUser to run which will redirect user to login page if not authenticated
    const { data: user, error, isLoading } = useGetCurrentUser();

    // Handle any redirects that are required
    useEffect(() => {
        if (!error) return;
        handleUserFetchError(error, navigate, location.pathname + location.search + location.hash, false, onLinkAccountRoute);
    }, [error, navigate, location.pathname, location.search, location.hash]);

    if (isLoading) return <SpinnerButton />;

    if (!user) return <div>Error fetching user</div>;

    return <>{children}</>;

}