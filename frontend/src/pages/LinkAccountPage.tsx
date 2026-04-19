import { useState, type JSX } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { requestAccountLink } from "../api/rest/users/mutate/requestAccountLink";
import { verifyAccountLink } from "../api/rest/users/mutate/verifyAccountLink";

export function LinkAccountPage(): JSX.Element {
    const navigate = useNavigate();
    const location = useLocation();
    const fromSetup = location.state?.fromSetup ?? false;

    const [step, setStep] = useState<"email" | "otp">("email");
    const [email, setEmail] = useState("");
    const [otp, setOtp] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    async function handleEmailSubmit(e: React.FormEvent): Promise<void> {
        e.preventDefault();
        setError("");
        const emailRegex: RegExp = /^.+@.+$/;
        if (!emailRegex.test(email)) {
            setError("Must be a valid email address");
            return;
        }
        setLoading(true);
        try {
            await requestAccountLink(email);
            setStep("otp");
        } catch (err: any) {
            if (err.status === 404) {
                setError("No account found with that recovery email. If you're new here, create an account instead.");
            } else {
                setError("Something went wrong. Please try again.");
            }
        } finally {
            setLoading(false);
        }
    }

    async function handleOtpSubmit(e: React.FormEvent): Promise<void> {
        e.preventDefault();
        setError("");
        if (!/^\d{6}$/.test(otp)) {
            setError("OTP must be a 6 digit number");
            return;
        }
        setLoading(true);
        try {
            await verifyAccountLink(otp);
            navigate("/", { replace: true });
        } catch (err: any) {
            if (err.status === 401) {
                // Session expired or invalid OTP - send back to email step to restart
                setStep("email");
                setOtp("");
                setError("Invalid or expired code. Please re-enter your recovery email to try again.");
            } else {
                setError("Something went wrong. Please try again.");
            }
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="flex justify-center min-h-screen w-full">
            <div className="flex flex-col w-full max-w-md min-h-screen p-6 gap-6">
                <h1 className="my-4 font-extrabold tracking-tight text-secondary">
                    {fromSetup ? "Link Another Provider" : "Link Account"}
                </h1>

                {step === "email" && (
                    <>
                        <p className="text-foreground">
                            {fromSetup
                                ? "To link this new login provider to your account, enter the recovery email you just registered with."
                                : "This login provider isn't linked to any account yet. If you have an existing account, enter your recovery email to link it."}
                        </p>
                        {!fromSetup && (
                            <p className="text-muted-foreground text-sm">
                                Alternatively, log in with an already linked provider and visit account settings to link providers manually.
                            </p>
                        )}
                        <form onSubmit={handleEmailSubmit} className="flex flex-col gap-4">
                            <label htmlFor="email" className="font-semibold text-foreground text-lg">Recovery email:</label>
                            <input
                                type="email"
                                id="email"
                                placeholder="your@email.com"
                                value={email}
                                required
                                onChange={(e) => setEmail(e.target.value)}
                                className="border border-border text-foreground font-semibold bg-input rounded-lg p-3 focus:outline-none focus:ring-2 placeholder:text-muted-foreground focus:ring-ring"
                            />
                            <button
                                type="submit"
                                disabled={loading}
                                className="bg-primary text-primary-foreground font-semibold py-2 px-4 rounded-lg hover:bg-primary/80 transition-colors cursor-pointer disabled:opacity-50"
                            >
                                {loading ? "Sending..." : "Send verification code"}
                            </button>
                            {!fromSetup && (
                                <button
                                    type="button"
                                    onClick={() => navigate("/user-setup", { replace: true, state: { firstTimeSetup: true } })}
                                    className="bg-secondary text-secondary-foreground font-semibold py-2 px-4 rounded-lg hover:bg-secondary/80 transition-colors cursor-pointer"
                                >
                                    New here? Create an account
                                </button>
                            )}
                        </form>
                    </>
                )}

                {step === "otp" && (
                    <>
                        <p className="text-foreground">
                            A 6 digit verification code has been sent to your recovery email. Enter it below. The code expires in 10 minutes.
                        </p>
                        <form onSubmit={handleOtpSubmit} className="flex flex-col gap-4">
                            <label htmlFor="otp" className="font-semibold text-foreground text-lg">Verification code:</label>
                            <input
                                type="text"
                                id="otp"
                                placeholder="123456"
                                value={otp}
                                required
                                minLength={6}
                                maxLength={6}
                                onChange={(e) => setOtp(e.target.value)}
                                className="border border-border text-foreground font-semibold bg-input rounded-lg p-3 focus:outline-none focus:ring-2 placeholder:text-muted-foreground focus:ring-ring"
                            />
                            <button
                                type="submit"
                                disabled={loading}
                                className="bg-primary text-primary-foreground font-semibold py-2 px-4 rounded-lg hover:bg-primary/80 transition-colors cursor-pointer disabled:opacity-50"
                            >
                                {loading ? "Verifying..." : "Verify code"}
                            </button>
                            <button
                                type="button"
                                onClick={() => { setStep("email"); setOtp(""); setError(""); }}
                                className="bg-muted text-muted-foreground font-semibold py-2 px-4 rounded-lg hover:bg-muted/80 transition-colors cursor-pointer"
                            >
                                Re-enter email
                            </button>
                        </form>
                    </>
                )}

                {error && <div className="error">{error}</div>}
            </div>
        </div>
    );
}