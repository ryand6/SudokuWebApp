import { useState } from "react";
import { Button } from "../../ui/button";
import { TimerCountdown } from "../../ui/custom/TimerCountdown";
import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import { LobbyPlayersPanel } from "../LobbyPlayersPanel";
import { LobbySettingsPanel } from "../LobbySettingsPanel";
import { LobbyChatPanel } from "../LobbyChatPanel";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";

export function LobbyMobileLayout({
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
    isMobile,
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
    isMobile: boolean,
    handleLeaveLobbyClick: () => void
}) {

    const [activePanel, setActivePanel] = useState<"players" | "settings" | "chat">("players");

    return (
        <div id="lobby-container" className="flex flex-col flex-1">
            <div id="lobby-header" className="flex flex-row justify-between">
                <h1 className="text-foreground-strong font-bold text-shadow m-3">{lobbyName}</h1>
                {countdownActive && countdownEndsAt && (
                    <TimerCountdown endTime={getEpochTimeFromTimestamp(countdownEndsAt)} />
                )}
                <Button className="m-2 cursor-pointer" onClick={handleLeaveLobbyClick} variant={"destructive"}>Leave Lobby</Button>
            </div>
            <div id="lobby-content" className="flex flex-col flex-1 gap-4 max-h-[70vh]">
                <div id="mobile-tabs">
                    <Button 
                        onClick={() => setActivePanel("players")}
                        className="cursor-pointer"
                    >
                        Players
                    </Button>
                    <Button 
                        onClick={() => setActivePanel("settings")}
                        className="cursor-pointer"
                    >
                        Settings
                    </Button>
                    <Button 
                        onClick={() => setActivePanel("chat")}
                        className="cursor-pointer"
                    >
                        Lobby Chat
                    </Button>
                </div>
                <div className="flex flex-row flex-1 min-h-0">
                    {/* Mobile only: render the active panel directly */}
                    <div className="flex-1 flex flex-col min-h-0 max-w-[95%] m-5">
                        {activePanel === "players" && 
                            <LobbyPlayersPanel 
                                lobbyId={lobbyId} 
                                userId={userId}
                                isPublic={isPublic}
                                hostId={hostId}
                                lobbyPlayers={lobbyPlayers}
                                countdownActive={countdownActive}
                                isMobile={isMobile}
                            />
                        }
                        {activePanel === "settings" && 
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
                        }
                        {activePanel === "chat" && 
                            <LobbyChatPanel 
                                lobbyId={lobbyId} 
                                userId={userId} 
                            />
                        }
                    </div>
                </div>
            </div>
        </div>
    )
}