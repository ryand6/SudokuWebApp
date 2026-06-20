import { wordToProperCase } from "@/utils/string/wordToProperCase";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import { useUpdateLobbyDifficulty } from "@/api/rest/lobby/settings/mutate/useUpdateLobbyDifficulty";
import { useUpdateLobbyTimeLimit } from "@/api/rest/lobby/settings/mutate/useUpdateLobbyTimeLimit";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import { Separator } from "../ui/separator";


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
    const updateTimeLimit = useUpdateLobbyTimeLimit();

    const difficultyOptions = ["Easy", "Medium", "Hard", "Extreme"];
    const durationOptions: { label: string; value: string }[] = [
        { label: "15 min", value: "Quick" },
        { label: "30 min", value: "Standard" },
        { label: "60 min", value: "Marathon" },
    ];

    const handleClickDifficulty = (value: string) => {
        if (countdownActive || updateDifficulty.isPending || value === wordToProperCase(difficulty)) {
            return;
        }
        const valueEnum = value.toUpperCase() as Difficulty;
        updateDifficulty.mutate({ lobbyId: lobbyId, userId: userId, difficulty: valueEnum });
    }

    const handleClickDuration = (value: string) => {
        if (countdownActive || updateTimeLimit.isPending || value === wordToProperCase(timeLimit)) {
            return;
        }
        const valueEnum = value.toUpperCase() as TimeLimitPreset;
        updateTimeLimit.mutate({lobbyId: lobbyId, userId: userId, timeLimit: valueEnum});
    }
 
    return (
        <div id="lobby-settings-panel" className="flex flex-col h-full w-full py-4 px-4 gap-3 min-h-0 overflow-y-auto">
            
            <div className="flex flex-col gap-2">
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
                                                ${wordToProperCase(difficulty) === option && "bg-secondary! text-secondary-foreground! border-secondary! font-semibold"}
                                                ${countdownActive && "opacity-40"}`}
                                    onClick={() => handleClickDifficulty(option)}
                                >
                                    {option}
                                </div>
                            )
                        }
                    </div>)
                : (
                    <div>
                        <span className="text-xl font-display text-accent-foreground font-semibold">{wordToProperCase(difficulty)}</span>
                    </div>
                )}
            </div>
            <Separator orientation="horizontal" className="bg-muted" />
            <div className="flex flex-col gap-2">
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
                                                ${wordToProperCase(timeLimit) === option.value && "bg-secondary! text-secondary-foreground! border-secondary! font-semibold"}
                                                ${countdownActive && "opacity-40"}`}
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
                        <span className="text-xl font-display text-accent-foreground font-semibold">{wordToProperCase(timeLimit)}</span>
                    </div>
                )}
            </div>
            <Separator orientation="horizontal" className="bg-muted" />

            <div className="flex flex-col gap-2">
                <div>
                    <span className="font-display text-muted-foreground font-semibold tracking-widest text-lg">GAME TYPE</span>
                </div>
                <div>
                    <span className="text-xl font-display text-accent-foreground font-semibold">{wordToProperCase(gameType)}</span>
                </div>
            </div>
            <Separator orientation="horizontal" className="bg-muted" />
            <div className="flex flex-col gap-2">
                <div>
                    <span className="font-display text-muted-foreground font-semibold tracking-widest text-lg">GAME MODE</span>
                </div>
                <div>
                    <span className="text-xl font-display text-accent-foreground font-semibold">{wordToProperCase(gameMode)}</span>
                </div>
            </div>
            
        </div>
    )
}