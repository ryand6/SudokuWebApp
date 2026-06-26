import { useState, type JSX } from "react";
import { SpinnerButton } from "../ui/custom/SpinnerButton";


interface UserFormProps {
    onSubmit: (username: string, recoveryEmail: string) => Promise<void>,
    submitLabel: string,
}

export function UserForm({
    onSubmit,
    submitLabel = "Submit"
}: UserFormProps): JSX.Element {
    const [username, setUsername] = useState("");
    const [recoveryEmail, setRecoveryEmail] = useState("");
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    // Validates username form field
    function validate(): boolean {
        if (!username.trim()) {
            setError("Username is required");
            return false;
        } else if (username.length < 3 || username.length > 10) {
            setError("Username must be between 3 and 10 characters long");
            return false;
        }
        if (!recoveryEmail.trim()) {
            setError("Recovery email is required");
            return false;
        }
        const emailRegex: RegExp = /^.+@.+$/;
        if (!emailRegex.test(recoveryEmail)) {
            setError("Must be a valid email address");
            return false;
        }
        return true;
    }

    async function handleSubmit(e: React.FormEvent): Promise<void> {
        e.preventDefault();
        // If there are form validation errors, don't submit and display the errors
        if (!validate()) return;
        setError("");
        setIsLoading(true);
        try {
            await onSubmit(username, recoveryEmail);
        } catch (err: any) {
            // Handle backend form validation errors
            if (err.status === 400) setError(err.message);
            else if (err.status === 409) setError("That username is already taken.");
            else if (err.status === 422) setError("Fix existing form errors.");
            else setError("Something went wrong whilst processing request.");
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <>
            {isLoading && <SpinnerButton />}
            <form onSubmit={handleSubmit} method="post" className="flex flex-col gap-4 w-full max-w-lg mx-auto">
                {/* display any errors found during attempted form submission */}
                {error && <div className="p-2 border-red-300 border-2 rounded-xl bg-red-200 text-destructive text-lg mb-1">{error}</div>}
                <div className="flex flex-col items-start">
                    <label htmlFor="username" className="font-semibold text-foreground mt-1 text-lg">Choose a username:</label>
                    <span className="text-xs text-muted">3-10 character limit</span>
                </div>
                
                <input
                    type="text"
                    id="username"
                    placeholder="Username"
                    value={username}
                    required
                    maxLength={20}
                    minLength={3}
                    onChange={(e) => setUsername(e.target.value)}
                    className="border border-border text-foreground font-semibold 
                                bg-input rounded-lg p-3 focus:outline-none focus:ring-2 
                                placeholder:text-muted-foreground focus:ring-ring"
                />
                <div className="flex flex-col items-start">
                    <label htmlFor="recoveryEmail" className="font-semibold text-foreground mt-1 text-lg">Recovery email:</label>
                    <span className="text-xs text-muted">Can be updated in account settings</span>
                </div>
                
                <p className="text-muted-foreground text-sm">
                    Used only for account recovery or linking new providers to your account. We store a pseudonymised hash of this address and cannot read it. 
                </p>
                <p className="text-red-400 text-sm mt-1">
                    Please ensure you keep it safe. If you lose access to all linked login providers and your recovery email, your account will be unrecoverable.
                </p>
                <input
                    type="email"
                    id="recoveryEmail"
                    placeholder="your@email.com"
                    value={recoveryEmail}
                    required
                    onChange={(e) => setRecoveryEmail(e.target.value)}
                    className="border border-border text-foreground font-semibold bg-input rounded-lg p-3 focus:outline-none focus:ring-2 placeholder:text-muted-foreground focus:ring-ring"
                />
                <button 
                    type="submit"
                    className="bg-primary text-primary-foreground font-semibold py-2 px-4 rounded-lg hover:bg-primary/80 transition-colors cursor-pointer"
                >
                    {submitLabel}
                </button>
            </form>
        </>
    );
}