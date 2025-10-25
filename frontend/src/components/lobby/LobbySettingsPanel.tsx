import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Label } from "../ui/label";
import { useEffect, useState } from "react";
import { updateLobbyDifficulty } from "@/api/lobby/updateLobbyDifficulty";
import type { Difficulty } from "@/types/enum/Difficulty";

export function LobbySettingsPanel({lobby, currentUser}: {lobby: LobbyDto, currentUser: UserDto}) {

    const [difficulty, setDifficulty] = useState<Difficulty>(lobby.difficulty);

    useEffect(() => {
        updateLobbyDifficulty(lobby.id, difficulty)
    }, [difficulty])
 
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
                    }}>
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
        </div>
    )
}