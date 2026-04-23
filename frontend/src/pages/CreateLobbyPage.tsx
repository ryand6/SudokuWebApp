import { processLobbySetup } from "@/api/rest/lobby/mutate/processLobbySetup";
import { Button } from "@/components/ui/button";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { Field, FieldContent, FieldDescription, FieldError, FieldGroup, FieldLabel, FieldLegend, FieldSeparator, FieldSet, FieldTitle } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import type { GameMode } from "@/types/enum/GameMode";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function CreateLobbyPage() {

    const [lobbyName, setLobbyName] = useState("");
    const [isPublic, setIsPublic] = useState(true);
    const [gameMode, setGameMode] = useState<GameMode>("CLASSIC");
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const navigate = useNavigate();

    // Validates form fields
    function validate(): boolean {
        if (!lobbyName.trim()) {
            setError("Lobby name is required");
            return false;
        } else if (lobbyName.length < 3 || lobbyName.length > 20) {
            setError("Lobby name must be between 3 and 20 characters long");
            return false;
        }
        return true;
    }

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        if (!validate()) return;
        setError("");
        try {
            setIsLoading(true);
            const response = await processLobbySetup(lobbyName, isPublic, gameMode);
            navigate(`/lobby/${response.id}`, { replace: true });
        } catch (err: any) {
            setIsLoading(false);
            // Display toast error message when the user is already part of an active lobby
            if (err.status === 401) toast.error(err.message, {containerId: "foreground"});
            // Handle backend form validation errors
            if (err.status === 400) setError(err.message);
            else setError("Something went wrong whilst processing this request");
        }
    }

    return (
        <div className="flex flex-1 justify-center items-center min-h-0 w-full">
            <div className="w-full max-w-[1200px] max-h-[90vh] border-border border-1 m-5 bg-card 
                            text-foreground rounded-md p-10 shadow-md overflow-y-auto min-h-0" 
            >
                {isLoading && <SpinnerButton />}
                <form onSubmit={handleSubmit} method="post">
                    <FieldSet>
                        <FieldLegend>Lobby Creation</FieldLegend>
                        <FieldDescription>Fill in the details of the lobby</FieldDescription>
                        <FieldSeparator />
                        <FieldGroup>
                            <Field orientation="responsive">
                                <FieldContent>
                                    <FieldLabel htmlFor="lobbyName">Lobby Name</FieldLabel>
                                    <FieldDescription>Provide the name of the lobby. This will be visible to other players.</FieldDescription>
                                </FieldContent>
                                <Input 
                                    id="lobbyName"
                                    type="text"
                                    placeholder="Enter Lobby Name"
                                    value={lobbyName}
                                    required
                                    maxLength={20}
                                    minLength={3}
                                    onChange={(e) => setLobbyName(e.target.value)}
                                    className="text-accent-foreground"
                                />
                            </Field>
                            {/* display any errors found during attempted form submission */}
                            {error && <FieldError>{error}</FieldError>}
                        </FieldGroup>
                        <FieldSeparator />
                        <FieldGroup>
                            <FieldSet>
                                <FieldLabel>Lobby Type</FieldLabel>
                                <FieldDescription>Select the lobby access type</FieldDescription>
                                <RadioGroup defaultValue="public" className="flex flex-col gap-2" onValueChange={(value) => setIsPublic(value === "public")}>
                                    <FieldLabel htmlFor="public-radio">
                                        <Field orientation="horizontal" className="cursor-pointer">
                                            <FieldContent>
                                                <FieldTitle>Public Lobby</FieldTitle>
                                                <FieldDescription>Lobby is open to the public and is discoverable via the dashboard</FieldDescription>
                                            </FieldContent>
                                            <RadioGroupItem value="public" id="public-radio" />
                                        </Field>
                                    </FieldLabel>
                                    <FieldLabel htmlFor="private-radio">
                                        <Field orientation="horizontal" className="cursor-pointer">
                                            <FieldContent>
                                                <FieldTitle>Private Lobby</FieldTitle>
                                                <FieldDescription>Lobby is only accessible to those with a valid access token and is not discoverable via the dashboard</FieldDescription>
                                            </FieldContent>
                                            <RadioGroupItem value="private" id="private-radio" />
                                        </Field>
                                    </FieldLabel>
                                </RadioGroup>
                            </FieldSet>
                        </FieldGroup>
                        <FieldSeparator />
                        <FieldGroup>
                            <FieldSet>
                                <FieldLabel>Game Mode</FieldLabel>
                                <FieldDescription>Select the game mode</FieldDescription>
                                <RadioGroup defaultValue="CLASSIC" className="flex flex-col md:flex-row gap-2" onValueChange={(value) => setGameMode(value.toUpperCase() as GameMode)}>
                                    <FieldLabel htmlFor="classic-radio">
                                        <Field orientation="horizontal" className="cursor-pointer">
                                            <FieldContent>
                                                <FieldTitle>Classic</FieldTitle>
                                                <FieldDescription>...classic game mode description...</FieldDescription>
                                            </FieldContent>
                                            <RadioGroupItem value="CLASSIC" id="classic-radio" />
                                        </Field>
                                    </FieldLabel>
                                    <FieldLabel htmlFor="domination-radio">
                                        <Field orientation="horizontal" className="cursor-pointer">
                                            <FieldContent>
                                                <FieldTitle>Domination</FieldTitle>
                                                <FieldDescription>...domination game mode description...</FieldDescription>
                                            </FieldContent>
                                            <RadioGroupItem value="DOMINATION" id="domination-radio" />
                                        </Field>
                                    </FieldLabel>
                                    <FieldLabel htmlFor="time-attack-radio">
                                        <Field orientation="horizontal" className="cursor-pointer">
                                            <FieldContent>
                                                <FieldTitle>Time Attack</FieldTitle>
                                                <FieldDescription>...time attack game mode description...</FieldDescription>
                                            </FieldContent>
                                            <RadioGroupItem value="TIMEATTACK" id="time-attack-radio" />
                                        </Field>
                                    </FieldLabel>
                                </RadioGroup>
                            </FieldSet>
                        </FieldGroup>
                        <FieldSeparator />
                        <Field orientation="responsive">
                            <Button type="submit" className="cursor-pointer">Create Lobby</Button>
                        </Field>
                    </FieldSet>
                </form>
            </div>    
        </div>
    )
}