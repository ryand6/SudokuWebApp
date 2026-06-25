import { Button } from "../ui/button";
import { isCurrentUserInLobby } from "@/utils/lobby/isCurrentUserInLobby";
import { computeTimeDifferenceMinutes } from "@/utils/time/timeDifference";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import { getDurationValue } from "@/utils/time/gameDurationUtils";

export function LobbyResultRow({ 
    lobbyId,
    userId,
    lobbyName,
    lobbyPlayers,
    hostName,
    inGame,
    difficulty,
    timeLimit,
    gameMode,
    gameType,
    createdAt,
    handleClick 
}: { 
    lobbyId: number,
    userId: number,
    lobbyName: string,
    lobbyPlayers: LobbyPlayerDto[],
    hostName: string,
    inGame: boolean,
    difficulty: Difficulty,
    timeLimit: TimeLimitPreset,
    gameMode: GameMode,
    gameType: GameType,
    createdAt: string,
    handleClick: (id: number) => void 
}) {

    return (
        <div className="flex flex-col gap-3 w-full rounded-lg border-muted border-2 py-3 px-4 text-sm hover:ring-4 hover:ring-ring/50">
            <div className="flex justify-between items-start">
                <div className="flex flex-col items-start">
                    <div className="font-semibold font-display text-foreground text-2xl">
                        {lobbyName}
                    </div>
                    <div className="text-md font-display text-muted-foreground">
                        Hosted by {hostName}
                    </div>
                </div>
                <div className="text-sm font-display text-muted-foreground">{computeTimeDifferenceMinutes(createdAt)} mins ago</div>
            </div>
            <div className="flex justify-start items-center gap-2">
                <div className={`py-1 px-2 rounded-full font-display ${gameType === "RANKED" ? "bg-sidebar-primary/30 text-sidebar-primary" : "bg-secondary/30 text-secondary"}`}>
                    {wordToProperCase(gameType)}
                </div>
                <div className="py-1 px-2 rounded-full font-display bg-muted/50 text-muted-foreground">
                    {wordToProperCase(gameMode)}
                </div>
                <div className="py-1 px-2 rounded-full font-display bg-muted/50 text-muted-foreground">
                    {wordToProperCase(difficulty)}
                </div>
                <div className="py-1 px-2 rounded-full font-display bg-muted/50 text-muted-foreground">
                    {wordToProperCase(getDurationValue(timeLimit))}
                </div>
                {inGame && 
                    <div className="py-1 px-2 rounded-full font-display bg-[#bee3f8] text-[#2a69ac]">
                        In Game
                    </div>
                }
            </div>
            <div className="items-stretch pt-2">
                <div className="flex w-full text-xs items-end">
                    <div className="flex-1 font-display text-muted-foreground">{lobbyPlayers.length} / 4 Players</div>
                    {!isCurrentUserInLobby(lobbyPlayers, userId) && <Button className="justify-self-end cursor-pointer bg-primary font-display" onClick={() => handleClick(lobbyId)} >Join Lobby</Button>}
                </div>
            </div>
        </div>
    )
}