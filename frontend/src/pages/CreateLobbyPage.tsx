import { processLobbySetup } from "@/api/lobby/processLobbySetup";
import { Button } from "@/components/ui/button";
import { Field, FieldContent, FieldDescription, FieldError, FieldGroup, FieldLabel, FieldLegend, FieldSeparator, FieldSet, FieldTitle } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export function CreateLobbyPage() {

    const [lobbyName, setLobbyName] = useState("");
    const [isPublic, setIsPublic] = useState(true);
    const [error, setError] = useState("");

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
            const response = await processLobbySetup(lobbyName, isPublic);
            navigate(`/lobby/${response.id}`, { replace: true });
        } catch (err: any) {
            // Handle backend form validation errors
            if (err.status === 400) setError(err.message);
            else setError("Something went wrong whilst processing this request");
        }
    }

    return (
        <div className="flex flex-1 justify-center items-center min-h-0">
            <div className="w-full max-w-[1200px] max-h-[90vh] border-border m-5 bg-white rounded-md p-10 shadow-md overflow-y-auto min-h-0" >
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
                                        <Field orientation="horizontal">
                                            <FieldContent>
                                                <FieldTitle>Public Lobby</FieldTitle>
                                                <FieldDescription>Lobby is open to the public and is discoverable via the dashboard</FieldDescription>
                                            </FieldContent>
                                            <RadioGroupItem value="public" id="public-radio" />
                                        </Field>
                                    </FieldLabel>
                                    <FieldLabel htmlFor="private-radio">
                                        <Field orientation="horizontal">
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
                        <Field orientation="responsive">
                            <Button type="submit">Create Lobby</Button>
                        </Field>
                    </FieldSet>
                </form>
            </div>    
        </div>
    )
}