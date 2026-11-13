
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";
import { useState } from "react";

export default function BetaAccess(
    { 
        onUnlocked 
    }: {
        onUnlocked: () => void
    }) {
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleSubmit(event: any) {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await fetch(
                `${import.meta.env.VITE_API_BASE_URL}/beta-access`,
                {
                    method: "POST",
                    credentials: "include",
                    headers: { 
                        "Content-Type": "application/json",
                        "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
                    },
                    body: JSON.stringify({ password })
                }
            );

            if (response.ok) {
                onUnlocked();
            } else if (response.status === 401) {
                setError("Incorrect beta password.");
            } else {
                setError("Unable to unlock beta access. Please try again.");
            }
        } catch {
            setError("Unable to connect. Please try again.");
        } finally {
            setLoading(false);
        }
    }

    return (
        <main style={{
            minHeight: "100vh",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            padding: "24px"
        }}>
            <form onSubmit={handleSubmit} style={{
                width: "100%",
                maxWidth: "380px",
                display: "flex",
                flexDirection: "column",
                gap: "16px"
            }}>
                <h1>KachiDoku Beta</h1>
                <p>Enter your beta access password to continue.</p>

                <input
                    type="password"
                    value={password}
                    onChange={event => setPassword(event.target.value)}
                    placeholder="Beta access password"
                    autoComplete="current-password"
                    required
                />

                {error && <p role="alert">{error}</p>}

                <button type="submit" disabled={loading}>
                    {loading ? "Checking..." : "Enter beta"}
                </button>
            </form>
        </main>
    );
}
