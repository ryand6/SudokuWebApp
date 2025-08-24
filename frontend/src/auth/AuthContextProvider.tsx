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

    // Gets and sets current user, otherwise sets error if user cannot be fetched
    const refreshUser = async () => {
        setLoadingUser(true);
        setUserFetchError(null);
        try {
            const userData: UserDto = await getCurrentUser();
            setUser(userData);
        } catch (err: any) {
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
        } catch (err: any) {
            setUserFetchError(err?.message || "Failed to logout");
        } finally {
            setLoadingUser(false);
        }
    };

    // Provides state variables and functions to children - variables are updated via the functions
    return (
        <AuthContext.Provider value={{ user, loadingUser, userFetchError, refreshUser, logout }}>
            {children}
        </AuthContext.Provider>
    );
}