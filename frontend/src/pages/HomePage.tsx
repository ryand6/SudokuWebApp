import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useGetCurrentUser } from "../hooks/users/useGetCurrentUser";
import { redirectPostLogin } from "../utils/routing/redirectPostLogin";

export function HomePage() {
    const { data: user } = useGetCurrentUser();
    const navigate = useNavigate();

    // When homepage is accessed, check if it was via a redirect following a login/account set up - if so, redirect to original URL
    useEffect(() => {
        redirectPostLogin(navigate)
    }, [user]);

    return <h1>HOME PAGE</h1>;
}