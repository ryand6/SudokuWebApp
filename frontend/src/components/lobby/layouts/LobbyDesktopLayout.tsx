import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import { TimerCountdown } from "@/components/ui/custom/TimerCountdown";
import { Button } from "@/components/ui/button";
import { LobbyPlayersPanel } from "../LobbyPlayersPanel";
import { LobbyChatPanel } from "../LobbyChatPanel";
import { LobbySettingsPanel } from "../LobbySettingsPanel";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";

export function LobbyDesktopLayout({
    lobbyId,
    lobbyName,
    userId,
    difficulty,
    isPublic,
    hostId,
    countdownActive,
    countdownEndsAt,
    timeLimit,
    gameMode,
    gameType,
    lobbyPlayers,
    handleLeaveLobbyClick
}: {
    lobbyId: number,
    lobbyName: string,
    userId: number,
    difficulty: Difficulty,
    isPublic: boolean,
    hostId: number,
    countdownActive: boolean,
    countdownEndsAt: string | null,
    timeLimit: TimeLimitPreset,
    gameMode: GameMode,
    gameType: GameType,
    lobbyPlayers: LobbyPlayerDto[],
    handleLeaveLobbyClick: () => void
}) {
    return (
        <div id="lobby-container" className="flex flex-col flex-1">
            <div id="lobby-header" className="flex flex-row justify-between">
                <h1 className="text-foreground-strong font-bold text-shadow m-3">{lobbyName}</h1>
                {countdownActive && countdownEndsAt && (
                    <TimerCountdown endTime={getEpochTimeFromTimestamp(countdownEndsAt)} />
                )}
                <Button className="m-2 cursor-pointer" onClick={handleLeaveLobbyClick} variant={"destructive"}>Leave Lobby</Button>
            </div>
            <div id="lobby-content" className="flex flex-col flex-1 gap-4 max-h-[75vh]">
                <div className="flex flex-row flex-1 min-h-0">
                    {/* Desktop only: split left/right columns */}
                    <div className="flex flex-1 min-h-0">
                        {/* Left column */}
                        <div className="flex flex-col flex-1 min-h-0 max-w-[50%]">
                            <div className="flex flex-col flex-1 min-h-0">
                                <LobbyPlayersPanel 
                                    lobbyId={lobbyId} 
                                    userId={userId}
                                    isPublic={isPublic}
                                    hostId={hostId}
                                    lobbyPlayers={lobbyPlayers}
                                    countdownActive={countdownActive}
                                />
                            </div>
                            <div className="flex flex-col flex-1 min-h-0">
                                <LobbySettingsPanel 
                                    lobbyId={lobbyId} 
                                    userId={userId}
                                    difficulty={difficulty}
                                    hostId={hostId}
                                    countdownActive={countdownActive}
                                    timeLimit={timeLimit}
                                    gameMode={gameMode}
                                    gameType={gameType}
                                />
                            </div>
                        </div>
                        {/* Right column */}
                        <div className="flex flex-col flex-1 min-h-0 max-w-[50%]">
                            <LobbyChatPanel 
                                lobbyId={lobbyId} 
                                userId={userId} 
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
