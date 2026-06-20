import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import { LobbyPlayersPanel } from "../LobbyPlayersPanel";
import { LobbyChatPanel } from "../LobbyChatPanel";
import { LobbySettingsPanel } from "../LobbySettingsPanel";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";
import type { GameMode } from "@/types/enum/GameMode";
import type { GameType } from "@/types/enum/GameType";
import type { LobbyPlayerDto } from "@/types/dto/entity/lobby/LobbyPlayerDto";
import { IconDoorExit } from "@tabler/icons-react";
import { useState } from "react";
import { LeaveLobbyAlertDialog } from "@/components/ui/custom/LeaveLobbyAlertDialog";
import BasicTimer from "@/components/ui/custom/BasicTimer";
import { IconUsers } from '@tabler/icons-react';
import { IconSettings } from '@tabler/icons-react';
import { IconMessageCircle } from '@tabler/icons-react';

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

    const [isAlertOpen, setIsAlertOpen] = useState(false);
    
    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

    return (
        <div id="lobby-container" className="flex flex-col h-full w-full">
            <div id="lobby-header" className="flex flex-row justify-between items-center bg-background px-8 py-2">
                <div className="flex items-center gap-3">
                    <h2 className="text-accent-foreground tracking-wider font-bold text-2xl font-display">
                        {lobbyName}
                    </h2>
                    <div className={`rounded-full py-1 px-3 text-white font-display text-xs ${isPublic ? "bg-[#22543d]" : "bg-[#742a2a]"}`}>
                        {isPublic ? "Public" : "Private"}
                    </div>
                </div>
                
                {countdownActive && countdownEndsAt && (
                    <span className="px-2 py-1 border-border border-2 rounded-lg bg-muted/10 font-mono text-xl">
                        <BasicTimer endTime={getEpochTimeFromTimestamp(countdownEndsAt)} unit="MINUTES" />
                    </span>
                )}
                <LeaveLobbyAlertDialog open={isAlertOpen} handleContinueClick={() => handleLeaveLobbyClick()} setOpen={setIsAlertOpen} />
           
                <div className="flex items-center justify-center rounded-md border-1 md:border-2
                        bg-background border-destructive/50 text-destructive cursor-pointer py-2 px-5
                        hover:bg-destructive/10" 
                        onClick={() => setIsAlertOpen(true)}>
                    <IconDoorExit size={iconSize} />
                </div>
            </div>
            <div id="lobby-content" className="flex flex-col w-full flex-1 min-h-0">
                <div className="flex flex-row w-full h-full">
                    {/* Desktop only: split left/right columns */}
                    <div className="flex w-full h-full">
                        {/* Left column */}
                        <div className="flex flex-col w-[40%]">
                            <div className="flex flex-col w-full h-[55%] border-border border-r-2">
                                <div className="flex items-center px-4 py-2 bg-sidebar justify-between">
                                    <div className="flex items-center gap-2">
                                        <span className="text-sidebar-primary">
                                            <IconUsers size={iconSize} />
                                        </span>
                                        <span className="tracking-wider font-semibold font-display text-primary-foreground">
                                            Players
                                        </span>
                                    </div>
                                    <span className="tracking-widest text-primary-foreground font-mono">{lobbyPlayers.length}/4</span>
                                </div>
                                <LobbyPlayersPanel 
                                    lobbyId={lobbyId} 
                                    userId={userId}
                                    isPublic={isPublic}
                                    hostId={hostId}
                                    lobbyPlayers={lobbyPlayers}
                                    countdownActive={countdownActive}
                                    isMobile={isMobile}
                                />
                            </div>
                            <div className="flex flex-col h-[45%] border-border border-t-2 border-r-2">
                                <div className="flex items-center px-4 py-2 bg-sidebar justify-between">
                                    <div className="flex items-center gap-2">
                                        <span className="text-sidebar-primary">
                                            <IconSettings size={iconSize} />
                                        </span>
                                        <span className="tracking-wider font-semibold font-display text-primary-foreground">
                                            Settings
                                        </span>
                                    </div>
                                    <span className="tracking-widest text-primary-foreground font-display">Host controls</span>
                                </div>
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
                        <div className="flex flex-col h-full w-[60%] min-h-0">
                            <div className="flex items-center px-4 py-2 bg-sidebar">
                                <div className="flex items-center gap-2">
                                    <span className="text-sidebar-primary">
                                        <IconMessageCircle size={iconSize} />
                                    </span>
                                    <span className="tracking-wider font-semibold font-display text-primary-foreground">
                                        Chat
                                    </span>
                                </div>
                            </div>
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
