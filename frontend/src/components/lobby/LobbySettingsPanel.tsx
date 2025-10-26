import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Label } from "../ui/label";
import { useEffect, useState } from "react";
import { updateLobbyDifficulty } from "@/api/lobby/updateLobbyDifficulty";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import { updateLobbyTimeLimit } from "@/api/lobby/updateLobbyTimeLimit";

export function LobbySettingsPanel({lobby, currentUser}: {lobby: LobbyDto, currentUser: UserDto}) {

    const [difficulty, setDifficulty] = useState<Difficulty>(lobby.difficulty);
    const [timeLimit, setTimeLimit] = useState<TimeLimitPreset>(lobby.timeLimit);

    useEffect(() => {
        updateLobbyDifficulty(lobby.id, difficulty)
    }, [difficulty])

    useEffect(() => {
        updateLobbyTimeLimit(lobby.id, timeLimit)
    }, [timeLimit])
 
    return (
        <div id="lobby-settings-panel">
            <h2>Game Settings</h2>
            <div>
                <div>
                    🎯 Difficulty 
                    <span id="difficulty-value"> {wordToProperCase(lobby.difficulty)}</span>
                </div>
                {currentUser.id === lobby.hostId && 
                <div>
                    <RadioGroup defaultValue={wordToProperCase(lobby.difficulty)} onValueChange={(value) => {
                        let valueEnum = value.toUpperCase() as Difficulty;
                        setDifficulty(valueEnum);
                    }} className="flex flex-row">
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
                {currentUser.id === lobby.hostId && 
                <div>
                    <RadioGroup defaultValue={wordToProperCase(lobby.timeLimit)} onValueChange={(value) => {
                        let valueEnum = value.toUpperCase() as TimeLimitPreset;
                        setTimeLimit(valueEnum);
                    }} className="flex flex-row">
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
        </div>
    )
}