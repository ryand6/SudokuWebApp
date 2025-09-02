import { type JSX } from "react";
import { processUserSetup } from "../api/user/processUserSetup";
import { useNavigate } from "react-router-dom";
import { getAuthContext } from "../auth/AuthContextProvider";
import { UserForm } from "../components/UserForm";

export function UserSetupPage(): JSX.Element {
    const { refreshUserAuth } = getAuthContext();
    const navigate = useNavigate();

    async function handleSetup(username: string): Promise<void> {
        await processUserSetup(username);
        // Set user global context variable by retrieving the newly created user from the backend
        await refreshUserAuth();
        // Redirect to homepage when finished setting up account, which will then redirect to referrer if there is one
        navigate("/", { replace: true });
    }

    return (
        <div className="flex justify-center min-h-screen">
            <div className="flex flex-col w-full max-w-md min-h-screen p-6">
                <h1 className="my-4 font-extrabold tracking-tight text-white">User Setup</h1>
                <UserForm onSubmit={handleSetup} submitLabel="Create Account"/>
            </div>
        </div>
    );
}