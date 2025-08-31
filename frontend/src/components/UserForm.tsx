import { useState, type JSX } from "react";
import { useNavigate } from "react-router-dom";
import { getAuthContext } from "../auth/AuthContextProvider";


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
            if (err.status === 409) setError("That username is already taken.");
            else if (err.status === 422) setError("Fix existing form errors.");
            else setError("Something went wrong whilst processing request.");
        }
    }

    return (
        <form onSubmit={handleSubmit} method="post">
            <label htmlFor="username">Choose a Username:</label>
            <input
                type="text"
                id="username"
                placeholder="Username"
                value={username}
                required
                maxLength={20} 
                onChange={(e) => setUsername(e.target.value)}
            />
            <button type="submit">{submitLabel}Create Account</button>
            {/* display any errors found during attempted form submission */}
            {error && <div className="error">{error}</div>}
        </form>
    );
}