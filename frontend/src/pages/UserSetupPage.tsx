import { useState, type JSX } from "react";
import { processUserSetup } from "../api/user/processUserSetup";
import { useNavigate } from "react-router-dom";

export function UserSetupPage(): JSX.Element {
    const [username, setUsername] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

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
            await processUserSetup(username);
            // Redirect to homepage when finished setting up account, which will then redirect to referrer if there is one
            navigate("/", { replace: true });
        } catch (err: any) {
            if (err.status === 409) setError("That username is already taken.");
            else if (err.status === 422) setError("Fix existing form errors.");
            else setError("Something went wrong whilst processing request.");
        }
    }

    return (
        <div>
            <h1>User Setup</h1>
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
                <button type="submit">Create Account</button>
                {error && <div className="error">{error}</div>}
            </form>
        </div>
        
    );
}