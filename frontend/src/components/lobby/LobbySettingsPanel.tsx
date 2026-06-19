import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Label } from "../ui/label";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import { useUpdateLobbyDifficulty } from "@/api/rest/lobby/settings/mutate/useUpdateLobbyDifficulty";
import { useUpdateLobbyTimeLimit } from "@/api/rest/lobby/settings/mutate/useUpdateLobbyTimeLimit";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import type { MouseEvent } from "react";


export function LobbySettingsPanel({
    lobbyId, 
    userId,
    difficulty,
    hostId,
    countdownActive,
    timeLimit,
    gameMode,
    gameType
}: {
    lobbyId: number, 
    userId: number,
    difficulty: Difficulty,
    hostId: number,
    countdownActive: boolean,
    timeLimit: TimeLimitPreset,
    gameMode: GameMode,
    gameType: GameType
}) {
    const updateDifficulty = useUpdateLobbyDifficulty();
    const updateLimitLimit = useUpdateLobbyTimeLimit();

    const difficultyOptions = ["Easy", "Medium", "Hard", "Extreme"];
    const durationOptions: { label: string; value: string }[] = [
        { label: "15 min", value: "Quick" },
        { label: "30 min", value: "Standard" },
        { label: "60 min", value: "Marathon" },
    ];

    const handleClickDifficulty = (value: string) => {
        if (value === wordToProperCase(difficulty)) {
            return;
        }
        const valueEnum = value.toUpperCase() as Difficulty;
        updateDifficulty.mutate({ lobbyId: lobbyId, userId: userId, difficulty: valueEnum });
    }

    const handleClickDuration = (value: string) => {
        if (value === wordToProperCase(timeLimit)) {
            return;
        }
        const valueEnum = value.toUpperCase() as TimeLimitPreset;
        updateLimitLimit.mutate({lobbyId: lobbyId, userId: userId, timeLimit: valueEnum});
    }
 
    return (
        <div id="lobby-settings-panel" className="flex flex-col h-full w-full py-6 px-4 gap-3">
            
            <div>
                <div>
                    <span className="font-display text-muted-foreground font-semibold tracking-widest text-lg">DIFFICULTY</span>
                </div>
                {userId === hostId ? 
                    (<div className="flex flex-wrap items-center gap-2 w-full">
                        {
                            difficultyOptions.map((option, index) => 
                                <div 
                                    key={index} 
                                    className={`rounded-full px-3 py-1 font-display text-muted-foreground border-2 border-muted cursor-pointer
                                                ${wordToProperCase(difficulty) === option && "bg-secondary! text-secondary-foreground! border-secondary! font-semibold"}`}
                                    onClick={() => handleClickDifficulty(option)}
                                >
                                    {option}
                                </div>
                            )
                        }
                    </div>)
                : (
                    <div>
                        <span className="text-lg font-display text-accent-foreground">{wordToProperCase(difficulty)}</span>
                    </div>
                )}
            </div>
            <div>
                <div>
                    <span className="font-display text-muted-foreground font-semibold tracking-widest text-lg">DURATION</span>
                </div>
                {userId === hostId && gameMode !== "TIMEATTACK" ?

                    (<div className="flex flex-wrap items-center gap-2 w-full">
                        {
                            durationOptions.map((option, index) => 
                                <div 
                                    key={index} 
                                    className={`rounded-full px-3 py-1 font-display text-muted-foreground border-2 border-muted cursor-pointer
                                                ${wordToProperCase(timeLimit) === option.value && "bg-secondary! text-secondary-foreground! border-secondary! font-semibold"}`}
                                    onClick={() => handleClickDuration(option.value)}
                                >
                                    {option.label}
                                </div>
                            )
                            
                        }
                        {gameType === "CASUAL" && 
                            <div className={`rounded-full px-3 py-1 font-display text-muted-foreground border-2 border-muted cursor-pointer
                                                ${wordToProperCase(timeLimit) === "Unlimited" && "bg-secondary! text-secondary-foreground! border-secondary! font-semibold"}`}
                                    onClick={() => handleClickDuration("Unlimited")}>
                                Unlimited
                            </div>
                        }
                    </div>)
                : (
                    <div>
                        <span className="text-lg font-display text-accent-foreground">{wordToProperCase(timeLimit)}</span>
                    </div>
                )}

                {/* <div>
                    <RadioGroup 
                        defaultValue={wordToProperCase(timeLimit)} 
                        onValueChange={(value) => {
                            let valueEnum = value.toUpperCase() as TimeLimitPreset;
                            updateLimitLimit.mutate({lobbyId: lobbyId, userId: userId, timeLimit: valueEnum});
                        }} 
                        disabled={countdownActive} 
                        className="flex flex-row"
                    >
                        <div>
                            <RadioGroupItem value="Quick" id="r-quick" />
                            <Label htmlFor="r-quick">15 min</Label>
                        </div>
                        <div>
                            <RadioGroupItem value="Standard" id="r-standard" />
                            <Label htmlFor="r-standard">30 min</Label>
                        </div>
                        <div>
                            <RadioGroupItem value="Marathon" id="r-marathon" />
                            <Label htmlFor="r-marathon">60 min</Label>
                        </div>
                        {gameType === "CASUAL" && (
                            <div>
                                <RadioGroupItem value="Unlimited" id="r-unlimited" />
                                <Label htmlFor="r-unlimited">Unlimited</Label>
                            </div>
                        )}
                    </RadioGroup>
                </div>
                } */}
            </div>

            <div className="flex gap-2 items-center">
                <span>
                    Game Type:
                </span>
                <span className="capitalize">
                    {wordToProperCase(gameType)}
                </span>
            </div>
            <div className="flex gap-2 items-center">
                <span>
                    Game Mode:
                </span>
                <span className="capitalize">
                    {wordToProperCase(gameMode)}
                </span>
            </div>
            
        </div>
    )
}