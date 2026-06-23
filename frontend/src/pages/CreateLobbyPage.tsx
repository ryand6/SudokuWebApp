import { processLobbySetup } from "@/api/rest/lobby/mutate/processLobbySetup";
import { Button } from "@/components/ui/button";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { Field, FieldContent, FieldDescription, FieldError, FieldGroup, FieldLabel, FieldLegend, FieldSet, FieldTitle } from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import { IconCheck } from "@tabler/icons-react";
import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function CreateLobbyPage() {

    const [lobbyName, setLobbyName] = useState("");
    const [isPublic, setIsPublic] = useState<boolean | null>(null);
    const [gameType, setGameType] = useState<GameType | null>(null);
    const [gameMode, setGameMode] = useState<GameMode | null>(null);
    const [currentStep, setCurrentStep] = useState(1);
    const [error, setError] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const navigate = useNavigate();

    const stepStates = useMemo(() => ({
        1: getStepState(1, currentStep),
        2: getStepState(2, currentStep),
        3: getStepState(3, currentStep),
        4: getStepState(4, currentStep),
    }), [currentStep]);

    type StepState = "completed" | "active" | "upcoming";

    function getStepState(step: number, currentStep: number): StepState {
        if (step < currentStep) return "completed";
        if (step === currentStep) return "active";
        return "upcoming";
    }

    // Validates form fields
    function validate(): boolean {
        if (!lobbyName.trim()) {
            setError("Lobby name is required");
            return false;
        } else if (lobbyName.length < 3 || lobbyName.length > 12) {
            setError("Lobby name must be between 3 and 12 characters long");
            return false;
        }
        if (isPublic === null) {
            setError("Please select a lobby type");
            return false;
        }
        if (gameType === null) {
            setError("Please select a game type");
            return false;
        }
        if (gameMode === null) {
            setError("Please select a game mode");
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
            if (isPublic === null || gameType === null || gameMode === null) return;
            const response = await processLobbySetup(lobbyName, isPublic, gameMode, gameType);
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
            <div className="w-full md:max-w-[1200px] md:max-h-[90vh] border-border md:border-1 md:bg-card 
                            md:mt-5 text-foreground md:rounded-md shadow-md overflow-y-auto min-h-0" 
            >
                <form onSubmit={handleSubmit} method="post">
                    <FieldSet>
                        <div className="bg-sidebar w-full p-5 sticky top-0 z-10">
                            <FieldLegend className="font-display text-xl! tracking-wide font-bold text-primary-foreground">Lobby Creation</FieldLegend>
                            <FieldDescription className="font-display">Set up a new lobby. You can only be part of one active lobby at a time.</FieldDescription>
                        </div>
                        {isLoading && <SpinnerButton />}
                        <div className="flex pt-5 px-5">
                            <div className="flex flex-col items-center">
                                <div 
                                    className={`rounded-full w-7 h-7 flex items-center justify-center text-sm font-display font-semibold 
                                                ${stepStates[1] === "completed" && "bg-sidebar-primary text-sidebar-primary-foreground"}
                                                ${stepStates[1] === "active" && "bg-secondary text-secondary-foreground"}
                                                ${stepStates[1] === "upcoming" && "bg-card text-muted-foreground border-1 border-muted"}`}>
                                    {stepStates[1] === "completed" ? <IconCheck /> : "1"}
                                </div>
                                <div className={`w-0.5 flex-1 my-1
                                                ${stepStates[1] === "completed" && "bg-sidebar-primary"}
                                                ${stepStates[1] === "active" && "bg-secondary"}
                                                ${stepStates[1] === "upcoming" && "bg-muted"}`}>

                                </div>
                            </div>
                            <div className={`px-5 flex-1 transition-opacity duration-200
                                            ${stepStates[1] === "upcoming" && "opacity-40 pointer-events-none select-none"}`}>
                                <FieldGroup className="gap-2">
                                    <Field orientation="horizontal">
                                        <FieldContent>
                                            <FieldLabel 
                                                htmlFor="lobbyName"
                                                className="font-display font-semibold tracking-wide text-foreground text-lg"
                                            >
                                                Lobby Name
                                            </FieldLabel>
                                        </FieldContent>
                                    </Field>
                                    <Field orientation="vertical">
                                        
                                        <Input 
                                            id="lobbyName"
                                            type="text"
                                            placeholder="Enter Lobby Name"
                                            value={lobbyName}
                                            required
                                            maxLength={12}
                                            minLength={3}
                                            onChange={(e) => {
                                                const newValue = e.target.value;
                                                setLobbyName(newValue);
                                                const trimmed = newValue.trim();
                                                if (trimmed.length >= 3 && trimmed.length <= 12 && currentStep === 1) {
                                                    setCurrentStep(prev => Math.min(prev + 1, 5));
                                                }
                                            }}
                                            className="text-accent-foreground"
                                        />

                                        <FieldContent>
                                            <FieldDescription>Visible to other players. 3-12 characters.</FieldDescription>
                                        </FieldContent>
                                    </Field>
                                    {/* display any errors found during attempted form submission */}
                                    {error && <FieldError className="font-display">{error}</FieldError>}
                                </FieldGroup>
                            </div>
                        </div>
                        <div className="flex px-5">
                            <div className="flex flex-col items-center">
                                 <div 
                                    className={`rounded-full w-7 h-7 flex items-center justify-center text-sm font-display font-semibold 
                                                ${stepStates[2] === "completed" && "bg-sidebar-primary text-sidebar-primary-foreground"}
                                                ${stepStates[2] === "active" && "bg-secondary text-secondary-foreground"}
                                                ${stepStates[2] === "upcoming" && "bg-card text-muted-foreground border-1 border-muted"}`}>
                                    {stepStates[2] === "completed" ? <IconCheck /> : "2"}
                                </div>
                                <div className={`w-0.5 flex-1 my-1
                                                ${stepStates[2] === "completed" && "bg-sidebar-primary"}
                                                ${stepStates[2] === "active" && "bg-secondary"}
                                                ${stepStates[2] === "upcoming" && "bg-muted"}`}>

                                </div>
                            </div>
                            <div className={`px-5 flex-1 transition-opacity duration-200
                                            ${stepStates[2] === "upcoming" && "opacity-40 pointer-events-none select-none"}`}>
                                <FieldGroup>
                                    <FieldSet>
                                        <FieldLabel
                                            className="font-display font-semibold tracking-wide text-foreground text-lg"
                                        >
                                            Lobby Type
                                        </FieldLabel>
                                        <RadioGroup 
                                            defaultValue=""
                                            className="flex flex-col gap-2" 
                                            onValueChange={(value) => {
                                                setIsPublic(value === "public");
                                                if (currentStep === 2) setCurrentStep(prev => Math.min(prev + 1, 5));
                                            }}
                                            disabled={stepStates[2] === "upcoming"}
                                        >
                                            <FieldLabel htmlFor="public-radio">
                                                <Field orientation="horizontal" className="cursor-pointer">
                                                    <RadioGroupItem value="public" id="public-radio" />
                                                    <FieldContent>
                                                        <FieldTitle>Public Lobby</FieldTitle>
                                                        <FieldDescription>Discoverable via the dashboard and open to all players</FieldDescription>
                                                    </FieldContent>
                                                </Field>
                                            </FieldLabel>
                                            <FieldLabel htmlFor="private-radio">
                                                <Field orientation="horizontal" className="cursor-pointer">
                                                    <RadioGroupItem value="private" id="private-radio" />
                                                    <FieldContent>
                                                        <FieldTitle>Private Lobby</FieldTitle>
                                                        <FieldDescription>Invite only via join code or URL</FieldDescription>
                                                    </FieldContent>
                                                </Field>
                                            </FieldLabel>
                                        </RadioGroup>
                                    </FieldSet>
                                </FieldGroup>
                            </div>
                        </div>
                        <div className="flex px-5">
                            <div className="flex flex-col items-center">
                                 <div 
                                    className={`rounded-full w-7 h-7 flex items-center justify-center text-sm font-display font-semibold 
                                                ${stepStates[3] === "completed" && "bg-sidebar-primary text-sidebar-primary-foreground"}
                                                ${stepStates[3] === "active" && "bg-secondary text-secondary-foreground"}
                                                ${stepStates[3] === "upcoming" && "bg-card text-muted-foreground border-1 border-muted"}`}>
                                    {stepStates[3] === "completed" ? <IconCheck /> : "3"}
                                </div>
                                <div className={`w-0.5 flex-1 my-1
                                                ${stepStates[3] === "completed" && "bg-sidebar-primary"}
                                                ${stepStates[3] === "active" && "bg-secondary"}
                                                ${stepStates[3] === "upcoming" && "bg-muted"}`}>

                                </div>
                            </div>
                            <div className={`px-5 flex-1 transition-opacity duration-200
                                            ${stepStates[3] === "upcoming" && "opacity-40 pointer-events-none select-none"}`}>
                                <FieldGroup>
                                    <FieldSet>
                                        <FieldLabel
                                            className="font-display font-semibold tracking-wide text-foreground text-lg"
                                        >
                                            Game Type
                                        </FieldLabel>
                                        <RadioGroup 
                                            defaultValue="" 
                                            className="flex flex-col gap-2" 
                                            onValueChange={(value) => {
                                                setGameType(value.toUpperCase() as GameType);
                                                if (currentStep === 3) setCurrentStep(prev => Math.min(prev + 1, 5));
                                            }}
                                            disabled={stepStates[3] === "upcoming"}
                                        >
                                            <FieldLabel htmlFor="ranked-radio">
                                                <Field orientation="horizontal" className="cursor-pointer">
                                                    <RadioGroupItem value="RANKED" id="ranked-radio" />
                                                    <FieldContent>
                                                        <FieldTitle>Ranked</FieldTitle>
                                                        <FieldDescription>Results tracked in leaderboards</FieldDescription>
                                                    </FieldContent>
                                                </Field>
                                            </FieldLabel>
                                            <FieldLabel htmlFor="casual-radio">
                                                <Field orientation="horizontal" className="cursor-pointer">
                                                    <RadioGroupItem value="CASUAL" id="casual-radio" />
                                                    <FieldContent>
                                                        <FieldTitle>Casual</FieldTitle>
                                                        <FieldDescription>Results are not tracked</FieldDescription>
                                                    </FieldContent>
                                                </Field>
                                            </FieldLabel>
                                        </RadioGroup>
                                    </FieldSet>
                                </FieldGroup>
                            </div>
                        </div>
                        <div className="flex px-5">
                            <div className="flex flex-col items-center">
                                 <div 
                                    className={`rounded-full w-7 h-7 flex items-center justify-center text-sm font-display font-semibold 
                                                ${stepStates[4] === "completed" && "bg-sidebar-primary text-sidebar-primary-foreground"}
                                                ${stepStates[4] === "active" && "bg-secondary text-secondary-foreground"}
                                                ${stepStates[4] === "upcoming" && "bg-card text-muted-foreground border-1 border-muted"}`}>
                                    {stepStates[4] === "completed" ? <IconCheck /> : "4"}
                                </div>
                            </div>
                            <div className={`px-5 flex-1 transition-opacity duration-200
                                            ${stepStates[4] === "upcoming" && "opacity-40 pointer-events-none select-none"}`}>
                                <FieldGroup>
                                    <FieldSet>
                                        <FieldLabel
                                            className="font-display font-semibold tracking-wide text-foreground text-lg"
                                        >
                                            Game Mode
                                        </FieldLabel>
                                        <FieldDescription>Select the game mode</FieldDescription>
                                        <RadioGroup 
                                            defaultValue="" 
                                            className="flex flex-col md:flex-row gap-2" 
                                            onValueChange={(value) => {
                                                setGameMode(value.toUpperCase() as GameMode);
                                                if (currentStep === 4) setCurrentStep(prev => Math.min(prev + 1, 5));
                                            }}
                                            disabled={stepStates[4] === "upcoming"}
                                        >
                                            <FieldLabel htmlFor="classic-radio">
                                                <Field orientation="horizontal" className="cursor-pointer">
                                                    <FieldContent>
                                                        <FieldTitle>Classic</FieldTitle>
                                                        <FieldDescription>Standard competitive sudoku where each player completes their own board. Correctness and speed are essential, with bonuses awarded to the player that answers a cell correctly with no mistakes first.</FieldDescription>
                                                    </FieldContent>
                                                    <RadioGroupItem value="CLASSIC" id="classic-radio" />
                                                </Field>
                                            </FieldLabel>
                                            <FieldLabel htmlFor="domination-radio">
                                                <Field orientation="horizontal" className="cursor-pointer">
                                                    <FieldContent>
                                                        <FieldTitle>Domination</FieldTitle>
                                                        <FieldDescription>Claim cells before your opponents to dominate the board. The board is shared between all players.</FieldDescription>
                                                    </FieldContent>
                                                    <RadioGroupItem value="DOMINATION" id="domination-radio" />
                                                </Field>
                                            </FieldLabel>
                                            <FieldLabel htmlFor="time-attack-radio">
                                                <Field orientation="horizontal" className="cursor-pointer">
                                                    <FieldContent>
                                                        <FieldTitle>Time Attack</FieldTitle>
                                                        <FieldDescription>Work with other players in a race against the clock to complete the board. The board is shared between all players.</FieldDescription>
                                                    </FieldContent>
                                                    <RadioGroupItem value="TIMEATTACK" id="time-attack-radio" />
                                                </Field>
                                            </FieldLabel>
                                        </RadioGroup>
                                    </FieldSet>
                                </FieldGroup>
                            </div>
                        </div>
                        <div className="flex px-5 pb-5">
                            <Field orientation="responsive">
                                <Button 
                                    type="submit" 
                                    className="cursor-pointer font-display font-semibold text-lg"
                                    disabled={stepStates[4] !== "completed"}
                                >
                                    Create Lobby
                                </Button>
                            </Field>
                        </div>
                    </FieldSet>
                </form>
            </div>    
        </div>
    )
}