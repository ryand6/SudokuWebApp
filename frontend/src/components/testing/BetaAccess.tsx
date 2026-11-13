
import { getCsrfTokenFromCookie } from "@/utils/auth/csrf";
import { useCallback, useEffect, useState } from "react";

type BetaAccessProps = {
    onUnlocked: () => void;
};

export default function BetaAccess({ onUnlocked }: BetaAccessProps) {
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [checking, setChecking] = useState(true);

    const apiUrl = import.meta.env.VITE_API_BASE_URL;

    const checkAccess = useCallback(async () => {
        try {
            const response = await fetch(
                `${apiUrl}/beta-access`,
                {
                    method: "GET",
                    credentials: "include",
                }
            );

            if (!response.ok) {
                throw new Error("Unable to check beta access.");
            }

            const result: { unlocked: boolean } = await response.json();

            if (result.unlocked) {
                onUnlocked();
            }
        } catch {
            setError("Unable to check access. Please refresh and try again.");
        } finally {
            setChecking(false);
        }
    }, [apiUrl, onUnlocked]);

    useEffect(() => {
        void checkAccess();
    }, [checkAccess]);

    async function handleSubmit(
        event: React.FormEvent<HTMLFormElement>
    ) {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            const response = await fetch(
                `${apiUrl}/beta-access`,
                {
                    method: "POST",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                        "X-XSRF-TOKEN": getCsrfTokenFromCookie() ?? "",
                    },
                    body: JSON.stringify({ password }),
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

    if (checking) {
        return <p>Checking beta access...</p>;
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
