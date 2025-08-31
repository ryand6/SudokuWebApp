import React, { createContext, useContext, useState, type JSX } from "react";
import type { AuthContextValue } from "../types/auth/AuthContextValue";
import { getCurrentUser } from "../api/user/currentUser";
import { userLogout } from "../api/user/userLogout";
import type { UserDto } from "../types/dto/UserDto";
import { useLocation, useNavigate } from "react-router-dom";


// Create context accessible throughout entire app
const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// Helper function to get AuthContext object anywhere within the AuthProvider component
export function getAuthContext(): AuthContextValue {
    const authContext = useContext(AuthContext);
    if (!authContext) {
        throw new Error("getAuthContext must be called from within AuthProvider component");
    }
    return authContext;
}

export function AuthContextProvider({ children } : { children: React.ReactNode }): JSX.Element {
    const [user, setUser] = useState<UserDto | null>(null);
    const [loadingUser, setLoadingUser] = useState<boolean>(false);
    const [userFetchError, setUserFetchError] = useState<string | null>(null);

    const navigate = useNavigate();
    const location = useLocation();

    // Gets and sets current user, otherwise sets error if user cannot be fetched
    const refreshUserAuth = async () => {
        setLoadingUser(true);
        setUserFetchError(null);
        try {
            const userData: UserDto = await getCurrentUser(navigate, location);
            setUser(userData);
        } catch (err: any) {
            const message = err?.message || "Failed to fetch user";

            if (!message.includes("Redirecting to login")) {
                setUser(null);
                setUserFetchError(message);
            }
            // TO REMOVE
            console.log("Error fetching data: " + err.message);
        } finally {
            setLoadingUser(false);
        }
    };

    const redirectPostLogin = () => {
        // Using session storage so that there isn't any overwriting of postLoginPath from different tabs - stores one per tab
        const referrer = sessionStorage.getItem("postLoginPath");
        // No redirect URL saved therefore stop
        if (!referrer) return;
        // Check for potential hamrful redirect URLs - referrer URLs should only be local page paths
        if (!referrer.startsWith("/")) {
            localStorage.removeItem("postLoginPath"); 
            return;
        }
        // Don't bounce back to login/account setup flows
        const pathnameOnly = referrer.split("?")[0];
        if (pathnameOnly === "/login" || pathnameOnly === "/user-setup") {
            localStorage.removeItem("postLoginPath");
            return;
        }
        sessionStorage.removeItem("postLoginPath");
        navigate(referrer, { replace: true });
    }

    // logs out user and sets their state to null, otherwise sets error if unable to logout user
    const logout = async () => {
        setLoadingUser(true);
        setUserFetchError(null); 
        try {
            await userLogout();
            setUser(null);
        } catch (err: any) {
            setUserFetchError(err?.message || "Failed to logout");
        } finally {
            setLoadingUser(false);
        }
    };

    // Provides state variables and functions to children - variables are updated via the functions
    return (
        <AuthContext.Provider value={{ user, loadingUser, userFetchError, refreshUserAuth, redirectPostLogin, logout }}>
            {children}
        </AuthContext.Provider>
    );
}