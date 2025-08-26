import { useEffect } from "react";
import { getAuthContext } from "../auth/AuthContextProvider";

export function HomePage() {
    const { user, redirectPostLogin } = getAuthContext();

    // When homepage is accessed, check if it was via a redirect following a login/account set up - if so, redirect to original URL
    useEffect(redirectPostLogin, [user]);

    return <h1>HOME PAGE</h1>;
}