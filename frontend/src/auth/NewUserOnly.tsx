import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

export function NewUserOnly({ children }: { children : React.ReactNode }) {
    const navigate = useNavigate();
    const location = useLocation();

    // If firstTimeSetup flag is passed to the route via navigate, use it to prevent toast notifications from being sent when a first time user sets up their account
    const firstTimeSetup = location.state?.firstTimeSetup ?? false;

    // check if the current route is the user setup page - pass flag to handleUserFetchError to prevent infinite redirects to user setup
    // const onUserSetupRoute: boolean = location.pathname === "/user-setup";

    useEffect(() => {
        if (!firstTimeSetup) {
            navigate("/dashboard", { replace: true });
        }
    }, []);

    if (!firstTimeSetup) return null;

    return <>{children}</>;
}