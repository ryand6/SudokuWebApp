import { useState, type JSX } from "react";


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

    // Validates username form field
    function validate(): boolean {
        if (!username.trim()) {
            setError("Username is required");
            return false;
        } else if (username.length < 3 || username.length > 20) {
            setError("Username must be between 3 and 20 characters long");
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
        try {
            await onSubmit(username, recoveryEmail);
        } catch (err: any) {
            // Handle backend form validation errors
            if (err.status === 400) setError(err.message);
            else if (err.status === 409) setError("That username is already taken.");
            else if (err.status === 422) setError("Fix existing form errors.");
            else setError("Something went wrong whilst processing request.");
        }
    }

    return (
        <form onSubmit={handleSubmit} method="post" className="flex flex-col gap-4 w-full max-w-md mx-auto">
            <label htmlFor="username" className="font-semibold text-foreground mt-1 text-lg">Choose a username:</label>
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
            <label htmlFor="recoveryEmail" className="font-semibold text-foreground mt-1 text-lg">Recovery email:</label>
            <p className="text-muted-foreground text-sm -mt-2">
                Used only for account recovery if you lose access to your login provider. We store a pseudonymised hash of this address and cannot read it.
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
            {/* display any errors found during attempted form submission */}
            {error && <div className="error">{error}</div>}
        </form>
    );
}