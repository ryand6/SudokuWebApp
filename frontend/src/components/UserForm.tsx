import { useState, type JSX } from "react";



interface UserFormProps {
    onSubmit: (username: string) => Promise<void>,
    submitLabel: string,
}

export function UserForm({
    onSubmit,
    submitLabel = "Submit"
}: UserFormProps): JSX.Element {
    const [username, setUsername] = useState("");
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
        return true;
    }

    async function handleSubmit(e: React.FormEvent): Promise<void> {
        e.preventDefault();
        // If there are form validation errors, don't submit and display the errors
        if (!validate()) return;
        setError("");
        try {
            await onSubmit(username);
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
            <label htmlFor="username" className="font-semibold text-gray-700 mt-1 text-lg">Choose a username:</label>
            <input
                type="text"
                id="username"
                placeholder="Username"
                value={username}
                required
                maxLength={20}
                minLength={3}
                onChange={(e) => setUsername(e.target.value)}
                className="border border-gray-300 font-semibold bg-white rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-400"
            />
            <button 
                type="submit"
                className="bg-blue-500 text-white font-semibold py-2 px-4 rounded-lg hover:bg-blue-600 transition-colors cursor-pointer"
            >
                {submitLabel}
            </button>
            {/* display any errors found during attempted form submission */}
            {error && <div className="error">{error}</div>}
        </form>
    );
}