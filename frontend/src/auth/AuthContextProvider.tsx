import React, { createContext, useContext, useEffect, useState, type JSX } from "react";
import type { AuthContextValue } from "../types/auth/AuthContextValue";
import type { UserDto } from "../dto/UserDto";
import { getCurrentUser } from "../utils/currentUser";
import { userLogout } from "../utils/userLogout";


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
    const [userSetupRequired, setUserSetupRequired] = useState<boolean>(false);
    const [loginRequired, setLoginRequired] = useState<boolean>(false);

    // Gets and sets current user, otherwise sets error if user cannot be fetched
    const refreshUser = async () => {
        setLoadingUser(true);
        setUserFetchError(null);
        try {
            const userData: UserDto = await getCurrentUser();
            setUser(userData);
            // reset flags
            setLoginRequired(false);
            setUserSetupRequired(false);
        } catch (err: any) {
            if (err.message.includes("account set up required")) {
                setUserSetupRequired(true);
            } else if (err.message.includes("OAuth2 login required")) {
                setLoginRequired(true);
            }
            setUser(null);
            setUserFetchError(err?.message || "Failed to fetch user");
        } finally {
            setLoadingUser(false);
        }
    };

    // logs out user and sets their state to null, otherwise sets error if unable to logout user
    const logout = async () => {
        setLoadingUser(true);
        setUserFetchError(null); 
        try {
            await userLogout();
            setUser(null);
            // reset flags
            setLoginRequired(false);
            setUserSetupRequired(false);
        } catch (err: any) {
            setUserFetchError(err?.message || "Failed to logout");
        } finally {
            setLoadingUser(false);
        }
    };

    // Set initial state values when AuthContextProvider is mounted
    useEffect(() => {
        refreshUser();
    }, []);

    // Provides state variables and functions to children - variables are updated via the functions
    return (
        <AuthContext.Provider value={{ user, loadingUser, userFetchError, userSetupRequired, loginRequired, refreshUser, logout }}>
            {children}
        </AuthContext.Provider>
    );
}