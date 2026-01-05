import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Label } from "../ui/label";
import { useEffect, useState } from "react";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import { Button } from "../ui/button";
import { ButtonCopy } from "../ui/custom/ButtonCopy";
import { JoinCodeAlertDialog } from "../ui/custom/JoinCodeAlertDialog";
import { useRequestJoinCode } from "@/hooks/lobby/useRequestJoinCode";
import { useGetActiveUserTokens } from "@/hooks/lobby/useGetActiveUserTokens";
import { useQueryClient } from "@tanstack/react-query";
import { useUpdateLobbyDifficulty } from "@/hooks/lobby/useUpdateLobbyDifficulty";
import { useUpdateLobbyTimeLimit } from "@/hooks/lobby/useUpdateLobbyTimeLimit";

export function LobbySettingsPanel({lobby, currentUser}: {lobby: LobbyDto, currentUser: UserDto}) {

    const [isAlertOpen, setIsAlertOpen] = useState<boolean>(false);

    const requestJoinCodeMutation = useRequestJoinCode();
    const queryClient = useQueryClient();
    const { data } = useGetActiveUserTokens(currentUser.id);
    const activeTokens = data?.activeTokens ?? [];

    const updateDifficulty = useUpdateLobbyDifficulty();
    const updateLimitLimit = useUpdateLobbyTimeLimit();

    // Interval to refresh active tokens display every minute
    useEffect(() => {
        const interval = setInterval(() => {
            queryClient.invalidateQueries({
                queryKey: ["user", currentUser.id, "tokens"]}
            );
        }, 60 * 1000);
        return () => clearInterval(interval);
    }, [currentUser.id, queryClient]);

    const handleClick = () => {
        if (activeTokens.length === 0) {
            requestJoinCode();
        } else {
            setIsAlertOpen(true);
        }
    }

    const requestJoinCode = async () => {
        await requestJoinCodeMutation.mutateAsync({ lobbyId: lobby.id, userId: currentUser.id });
    };
 
    return (
        <div id="lobby-settings-panel" className="flex flex-col flex-1 lobby-card">
            {!lobby.isPublic && <JoinCodeAlertDialog open={isAlertOpen} handleContinueClick={requestJoinCode} setOpen={setIsAlertOpen} />}
            <h2 className="card-header">Game Settings</h2>
            <div>
                <div>
                    🎯 Difficulty 
                    <span id="difficulty-value"> {wordToProperCase(lobby.difficulty)}</span>
                </div>
                {currentUser.id === lobby.host.id && 
                <div>
                    <RadioGroup 
                        defaultValue={wordToProperCase(lobby.difficulty)} 
                        onValueChange={(value) => {
                            let valueEnum = value.toUpperCase() as Difficulty;
                            updateDifficulty.mutate({ lobbyId: lobby.id, userId: currentUser.id, difficulty: valueEnum });
                        }} 
                        disabled={lobby.settingsLocked} 
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
                    <span id="duration-value"> {wordToProperCase(lobby.timeLimit)}</span>
                </div>
                {currentUser.id === lobby.host.id && 
                <div>
                    <RadioGroup 
                        defaultValue={wordToProperCase(lobby.timeLimit)} 
                        onValueChange={(value) => {
                            let valueEnum = value.toUpperCase() as TimeLimitPreset;
                            updateLimitLimit.mutate({lobbyId: lobby.id, userId: currentUser.id, timeLimit: valueEnum});
                        }} 
                        disabled={lobby.settingsLocked} 
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
                        <div>
                            <RadioGroupItem value="Unlimited" id="r-unlimited" />
                            <Label htmlFor="r-unlimited">Unlimited</Label>
                        </div>
                    </RadioGroup>
                </div>
                }
            </div>
            {!lobby.isPublic && 
            <div className="mt-2">
                <h2>Lobby Join Code</h2>
                <div className="flex flex-row items-center gap-4 mt-2">
                    <div className="flex flex-row justify-between w-[75%] h-[100%] border-1 py-1 px-2 text-gray-400">
                        <span className="truncate max-w-full">{activeTokens?.[activeTokens.length - 1]?.token || "Generated join code"}</span>
                        <ButtonCopy text={activeTokens?.[activeTokens.length - 1]?.token || ""} className="text-black"/>
                    </div>
                    <Button id="join-private-btn" className="w-[20%] cursor-pointer whitespace-normal text-wrap" onClick={handleClick}>Generate Code</Button>
                </div>
                <div className="mt-2 max-h-40 border p-2 rounded overflow-y-auto">
                    <h2 className="border-b mb-2 py-1">Active Join Codes:</h2>
                    {activeTokens?.length ? (
                        activeTokens.map((token) => {
                            // Calculate the minutes left to expiry on the token, rounded up to the nearest minute
                            const minutesLeft = Math.max(0, Math.ceil((token.expiresAt - Date.now()) / 60000));
                            return (
                                <div key={token.token} className="flex justify-between py-1 px-2 mb-2 border-b last:border-b-0">
                                    <span className="break-all w-[70%]">{token.token}</span>
                                    <ButtonCopy text={token.token} />
                                    <span className="text-gray-500">{minutesLeft} min left</span>
                                </div>
                            );
                        })
                    ) : (
                        <span className="text-gray-400">No active join codes</span>
                    )}
                </div>
            </div>
            }
        </div>
    )
}