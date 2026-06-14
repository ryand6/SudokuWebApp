import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Label } from "../ui/label";
import { useState } from "react";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import { Button } from "../ui/button";
import { ButtonCopy } from "../ui/custom/ButtonCopy";
import { JoinCodeAlertDialog } from "../ui/custom/JoinCodeAlertDialog";
import { useQueryClient } from "@tanstack/react-query";
import { useRefreshActiveTokensList } from "@/hooks/lobby/useRefreshActiveTokensList";
import { useRequestJoinCode } from "@/api/rest/lobby/token/mutate/useRequestJoinCode";
import { useGetActiveUserTokens } from "@/api/rest/lobby/token/query/useGetActiveUserTokens";
import { useUpdateLobbyDifficulty } from "@/api/rest/lobby/settings/mutate/useUpdateLobbyDifficulty";
import { useUpdateLobbyTimeLimit } from "@/api/rest/lobby/settings/mutate/useUpdateLobbyTimeLimit";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";

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
 
    return (
        <div id="lobby-settings-panel" className="flex flex-col flex-1 lobby-card">
            
            <h2 className="card-header">Game Settings</h2>
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
            <div>
                <div>
                    🎯 Difficulty 
                    <span id="difficulty-value"> {wordToProperCase(difficulty)}</span>
                </div>
                {userId === hostId && 
                <div>
                    <RadioGroup 
                        defaultValue={wordToProperCase(difficulty)} 
                        onValueChange={(value) => {
                            let valueEnum = value.toUpperCase() as Difficulty;
                            updateDifficulty.mutate({ lobbyId: lobbyId, userId: userId, difficulty: valueEnum });
                        }} 
                        disabled={countdownActive} 
                        className="flex flex-row"
                    >
                        <div>
                            <RadioGroupItem value="Easy" id="r-easy" />
                            <Label htmlFor="r-easy">Easy</Label>
                        </div>
                        <div>
                            <RadioGroupItem value="Medium" id="r-medium" />
                            <Label htmlFor="r-medium">Medium</Label>
                        </div>
                        <div>
                            <RadioGroupItem value="Hard" id="r-hard" />
                            <Label htmlFor="r-hard">Hard</Label>
                        </div>
                        <div>
                            <RadioGroupItem value="Extreme" id="r-extreme" />
                            <Label htmlFor="r-extreme">Extreme</Label>
                        </div>
                    </RadioGroup>
                </div>
                }
            </div>
            <div>
                <div>
                    ⏱️ Game Duration
                    <span id="duration-value"> {wordToProperCase(timeLimit)}</span>
                </div>
                {userId === hostId && gameMode !== "TIMEATTACK" &&
                <div>
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
                }
            </div>
            
        </div>
    )
}