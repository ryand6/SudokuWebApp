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
import BasicTimer from "@/components/ui/custom/BasicTimer";
import { LeaveLobbyAlertDialog } from "@/components/ui/custom/LeaveLobbyAlertDialog";
import { IconDoorExit, IconMessageCircle, IconSettings, IconUsers } from "@tabler/icons-react";
import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from "@/components/ui/accordion";

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

    const [isAlertOpen, setIsAlertOpen] = useState(false);
    
    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

    const [activePanel, setActivePanel] = useState<"players" | "settings" | "chat">("players");

    return (
        <div id="lobby-container" className="flex flex-col flex-1">
            <div id="lobby-header" className="flex flex-row justify-between items-center bg-background px-8 py-2">
                <div className="flex items-center gap-3">
                    <h2 className="text-accent-foreground tracking-wider font-bold text-3xl font-display">
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
            <div id="lobby-content" className="flex flex-col flex-1 gap-4">

                <div className="flex flex-col flex-1 min-h-0 p-2.5">
                    <Accordion
                        type="single"
                        collapsible
                        defaultValue="players"
                        className="flex flex-col gap-2"
                    >
                        {/* Players */}
                        <AccordionItem
                            value="players"
                            className="rounded-lg border-1 border-muted overflow-hidden data-[state=open]:border-border"
                        >
                            <AccordionTrigger
                                className="px-3 py-2.5 gap-2 hover:no-underline
                                        bg-card text-accent-foreground rounded-b-none
                                        data-[state=open]:bg-sidebar data-[state=open]:text-sidebar-foreground"
                            >
                                <div className="flex items-center px-4 py-2">
                                    <div className="flex items-center gap-2">
                                        <span className="text-sidebar-primary">
                                            <IconUsers size={24} />
                                        </span>
                                        <div className="flex flex-col items-start">
                                            <span className="tracking-wider font-semibold font-display text-2xl">
                                                Players
                                            </span>
                                             <span className="tracking-widest font-mono">{lobbyPlayers.length}/4 joined</span>
                                        </div>
                                    </div>
                                </div>
                            </AccordionTrigger>
                            <AccordionContent className="bg-background border-t-1 border-border px-3 pt-3 pb-3.5">
                                <LobbyPlayersPanel 
                                    lobbyId={lobbyId} 
                                    userId={userId}
                                    isPublic={isPublic}
                                    hostId={hostId}
                                    lobbyPlayers={lobbyPlayers}
                                    countdownActive={countdownActive}
                                    isMobile={isMobile} 
                                />
                            </AccordionContent>
                        </AccordionItem>
    
                        {/* Settings */}
                        <AccordionItem
                            value="settings"
                            className="rounded-lg border-1 border-muted overflow-hidden data-[state=open]:border-border"
                        >
                            <AccordionTrigger
                                className="px-3 py-2.5 gap-2 hover:no-underline
                                        bg-card text-accent-foreground rounded-b-none
                                        data-[state=open]:bg-sidebar data-[state=open]:text-sidebar-foreground"
                            >
                                <div className="flex items-center px-4 py-2 justify-between">
                                    <div className="flex items-center gap-2">
                                        <span className="text-sidebar-primary">
                                            <IconSettings size={24} />
                                        </span>
                                        <div className="flex flex-col items-start">
                                            <span className="tracking-wider font-semibold font-display text-2xl">
                                                Settings
                                            </span>
                                             <span className="tracking-widest font-display">Host controls</span>
                                        </div>
                                    </div>
                                </div>
                            </AccordionTrigger>
                            <AccordionContent className="bg-background border-t-1 border-border px-3 pt-3 pb-3.5">
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
                            </AccordionContent>
                        </AccordionItem>
    
                        {/* Chat */}
                        <AccordionItem
                            value="chat"
                            className="rounded-lg border-1! border-muted overflow-hidden data-[state=open]:border-border"
                        >
                            <AccordionTrigger
                                className="px-3 py-2.5 gap-2 hover:no-underline
                                        bg-card text-accent-foreground rounded-b-none
                                        data-[state=open]:bg-sidebar data-[state=open]:text-sidebar-foreground"
                            >
                                <div className="flex items-center px-4 py-2">
                                    <div className="flex items-center gap-2">
                                        <span className="text-sidebar-primary">
                                            <IconMessageCircle size={24} />
                                        </span>
                                        <span className="tracking-wider font-semibold font-display text-2xl">
                                            Chat
                                        </span>
                                    </div>
                                </div>
                            </AccordionTrigger>
                            <AccordionContent className="bg-background border-t-1 border-border px-3 pt-3 pb-3.5">
                                <div className="h-[360px] flex flex-col">
                                    <LobbyChatPanel lobbyId={lobbyId} userId={userId} />
                                </div>
                            </AccordionContent>
                        </AccordionItem>
                    </Accordion>
                </div>
            </div>
        </div>
    )
}