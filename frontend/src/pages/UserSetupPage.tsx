import { useState, type JSX } from "react";
import { processUserSetup } from "../api/rest/users/mutate/processUserSetup";
import { useNavigate } from "react-router-dom";
import { UserForm } from "../components/users/UserForm";

export function UserSetupPage(): JSX.Element {
    const navigate = useNavigate();
    const [setupComplete, setSetupComplete] = useState(false);

    async function handleSetup(username: string, recoveryEmail: string): Promise<void> {
        await processUserSetup(username, recoveryEmail);
        setSetupComplete(true);
    }

    if (setupComplete) {
        return (
            <div className="flex justify-center min-h-screen w-full">
                <div className="flex flex-col w-full max-w-md min-h-screen p-6 gap-6">
                    <h1 className="my-4 font-extrabold tracking-tight text-secondary">Account Created</h1>
                    <p className="text-foreground">
                        Your account has been created successfully. Would you like to link any other login providers now?
                    </p>
                    <p className="text-muted-foreground text-sm">
                        If you skip this step, you can still link providers later from account settings. 
                    </p>
                    <p className="text-muted-foreground text-sm">
                        If you lose access to a linked provider, log in with any other provider and use your recovery email to link it to the account. 
                        If you lose access to all linked providers, your account will be unrecoverable.
                    </p>
                    <div className="flex flex-col gap-3">
                        <button
                            onClick={() => navigate("/link-account", { replace: true, state: { fromSetup: true } })}
                            className="bg-primary text-primary-foreground font-semibold py-2 px-4 rounded-lg hover:bg-primary/80 transition-colors cursor-pointer"
                        >
                            Link another provider
                        </button>
                        <button
                            // Redirect to homepage when finished setting up account, which will then redirect to referrer if there is one
                            onClick={() => navigate("/", { replace: true })}
                            className="bg-muted text-muted-foreground font-semibold py-2 px-4 rounded-lg hover:bg-muted/80 transition-colors cursor-pointer"
                        >
                            Skip for now
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="flex justify-center min-h-screen w-full">
            <div className="flex flex-col w-full max-w-md min-h-screen p-6">
                <h1 className="my-4 font-extrabold tracking-tight text-secondary">User Setup</h1>
                <UserForm onSubmit={handleSetup} submitLabel="Create Account"/>
            </div>
        </div>
    );
}