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
        <div>
            <h1>User Setup</h1>
            <UserForm onSubmit={handleSetup} submitLabel="Create Account"/>
        </div>
    );
}