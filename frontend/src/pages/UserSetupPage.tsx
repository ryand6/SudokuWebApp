import { type JSX } from "react";
import { processUserSetup } from "../api/users/processUserSetup";
import { useNavigate } from "react-router-dom";
import { UserForm } from "../components/users/UserForm";

export function UserSetupPage(): JSX.Element {
    const navigate = useNavigate();

    async function handleSetup(username: string): Promise<void> {
        await processUserSetup(username);
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