import { useNavigate } from "react-router-dom";
import { processUsernameUpdate } from "../api/rest/users/mutate/processUsernameUpdate";
import { useGetCurrentUser } from "../api/rest/users/query/useGetCurrentUser";
import { useState } from "react";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";

export function UsernameUpdatePage() {
	const { data: user } = useGetCurrentUser();
	const navigate = useNavigate();
	const [username, setUsername] = useState("");
	const [error, setError] = useState("");
	const [isLoading, setIsLoading] = useState(false);

	function validate(): boolean {
		if (!username.trim()) {
			setError("Username is required");
			return false;
		} else if (username.length < 3 || username.length > 10) {
			setError("Username must be between 3 and 10 characters long");
			return false;
		}
		return true;
	}

	async function handleAmend(username: string) {
		await processUsernameUpdate(username);
		navigate("/dashboard", { replace: true });
	}

	async function handleSubmit(e: React.FormEvent): Promise<void> {
        e.preventDefault();
        // If there are form validation errors, don't submit and display the errors
        if (!validate()) return;
        setError("");
        setIsLoading(true);
        try {
            await handleAmend(username);
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
		<div className="flex justify-center min-h-screen w-full font-display">
			<div className="flex flex-col w-full max-w-lg min-h-screen p-6">
				<h1 className="my-4 text-4xl font-bold tracking-tight text-foreground">Update Username</h1>
				<label className="font-semibold text-gray-700 text-lg">Current username:</label>
				<div className="border border-gray-400 rounded-lg p-3 mt-4 mb-6 bg-gray-500">{user?.username ?? ""}</div>
				{isLoading && <SpinnerButton />}
				<form onSubmit={handleSubmit} method="post" className="flex flex-col gap-8 w-full max-w-lg mx-auto">
					{/* display any errors found during attempted form submission */}
					{error && <div className="p-2 border-red-300 border-2 rounded-xl bg-red-200 text-destructive text-lg mb-1">{error}</div>}
					<div className="flex flex-col gap-3">
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
						<span className="text-destructive text-sm">Your username is visible to other players. Don't use your real name or other personal information.</span>
					</div>
					<button 
						type="submit"
						className="bg-primary text-primary-foreground font-semibold py-2 px-4 rounded-lg hover:bg-primary/80 transition-colors cursor-pointer"
					>
						Update Username
					</button>
				</form>
			</div>
		</div>
	);
}