import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useCurrentUser } from "../hooks/useCurrentUser";
import { redirectPostLogin } from "../utils/redirectPostLogin";

export function HomePage() {
    const { data: user } = useCurrentUser();
    const navigate = useNavigate();

    // When homepage is accessed, check if it was via a redirect following a login/account set up - if so, redirect to original URL
    useEffect(() => {
        redirectPostLogin(navigate)
    }, [user]);

    return <h1>HOME PAGE</h1>;
}