import type { GamePlayers } from "@/types/game/GameTypes";
import { Modal } from "../../ui/custom/Modal";
import { useEffect, useState, type Dispatch, type SetStateAction } from "react";
import { type UseMutateFunction } from "@tanstack/react-query";
import { LeaveGameAlertDialog } from "../../ui/custom/LeaveGameAlertDialog";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LeaveGameRequestDto } from "@/types/dto/request/LeaveGameRequestDto";
import { HUDStats } from "./modals/stats/HUDStats";
import { HUDHeatMaps } from "./modals/heatmap/HUDHeatMaps";
import { HUDGameEventLog } from "./modals/log/HUDGameEventLog";
import { HUDGameChat } from "./modals/chat/HUDGameChat";
import { IconChartBar } from '@tabler/icons-react';
import { IconFlame } from '@tabler/icons-react';
import { IconLogs } from '@tabler/icons-react';
import { IconMessageCircle } from '@tabler/icons-react';
import { IconDoorExit } from '@tabler/icons-react';
import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { GameMode } from "@/types/enum/GameMode";

export function GameHUD(
    {
        userId,
        gameId,
        lobbyId,
        gamePlayers, 
        currentStreak,
        leaveGameHandler,
        isMobile,
        playerColours,
        gameMode
    }: {
        userId: number,
        gameId: number,
        lobbyId: number,
        gamePlayers: GamePlayers, 
        currentStreak: number,
        isMobile: boolean,
        leaveGameHandler: {
            mutate: UseMutateFunction<GameDto | null, Error, LeaveGameRequestDto, unknown>;
            isLeaving: boolean;
        },
        playerColours: Record<number, PlayerColour> | undefined,
        gameMode: GameMode
    }
) {
    const [isStatsModalOpen, setStatsModalOpen] = useState(false);
    const [isHeatMapModalOpen, setHeatMapModalOpen] = useState(false);
    const [isGameLogModalOpen, setGameLogModalOpen] = useState(false);
    const [isGameChatModalOpen, setGameChatModalOpen] = useState(false);
    const [isAlertOpen, setIsAlertOpen] = useState(false);

    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

    useEffect(() => {
        document.getElementById("root")?.classList.remove("blur-sm");
    }, []);

    const openModal = (setter: Dispatch<SetStateAction<boolean>>) => {
        setter(true);
        document.getElementById("root")?.classList.add("blur-sm");
    };

    const closeModal = (setter: Dispatch<SetStateAction<boolean>>) => {
        setter(false);
        document.getElementById("root")?.classList.remove("blur-sm");
    };

    return (
        <div 
            className="bg-card h-auto w-full md:h-[50%]"
        >   
            <div className="flex md:flex-col justify-evenly h-full gap-2 md:gap-5 px-2 py-4">
                {
                    !isMobile && (
                        <div className="justify-start">
                            <span className="font-display text-muted-foreground font-semibold tracking-widest text-2xl">
                                GAME
                            </span>
                        </div>
                    )
                }
                <div 
                    onClick={() => openModal(setStatsModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 md:border-2 text-accent-foreground text-xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary py-2"
                >
                    <span><IconChartBar size={iconSize} stroke={iconStroke} /></span>
                    <span>Stats</span>   
                </div>
                <div 
                    onClick={() => openModal(setHeatMapModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 md:border-2  text-accent-foreground text-xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary py-2"
                >
                    <span><IconFlame size={iconSize} stroke={iconStroke} /></span>
                    <span>Heat Maps</span>   
                </div>
                <div 
                    onClick={() => openModal(setGameLogModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 md:border-2  text-accent-foreground text-xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary py-2"
                >
                    <span><IconLogs size={iconSize} stroke={iconStroke} /></span>
                    <span>Log</span>  
                </div>
                <div 
                    onClick={() => openModal(setGameChatModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 md:border-2 text-accent-foreground text-xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary py-2"
                >
                    <span><IconMessageCircle size={iconSize} stroke={iconStroke} /></span>
                    <span>Chat</span>
                </div>
                <LeaveGameAlertDialog open={isAlertOpen} handleContinueClick={() => leaveGameHandler.mutate({gameId, userId, lobbyId})} setOpen={setIsAlertOpen} />
                <div className="flex items-center justify-center w-full h-full rounded-md border-1 md:border-2 
                            bg-background border-destructive/50 text-destructive cursor-pointer py-2
                            hover:bg-destructive/10" 
                            onClick={() => setIsAlertOpen(true)}>
                    <IconDoorExit size={iconSize} />
                </div>
        
            </div>

            <Modal 
                isOpen={isStatsModalOpen} 
                onClose={() => closeModal(setStatsModalOpen)}
                className="w-[80%]! h-[80%]! md:h-[70%]! top-[10%]! md:top-[15%]! left-[10%]! !blur-none z-50"
            >
                <HUDStats 
                    userId={userId}
                    gamePlayers={gamePlayers}
                    currentStreak={currentStreak} 
                />
            </Modal>


            <Modal 
                isOpen={isHeatMapModalOpen} 
                onClose={() => closeModal(setHeatMapModalOpen)}
                className="w-[80%]! h-[80%]! md:h-[70%]! top-[10%]! md:top-[15%]! left-[10%]! !blur-none z-50"
            >
                <HUDHeatMaps 
                    userId={userId}
                    gamePlayers={gamePlayers}
                    isMobile={isMobile}
                />
            </Modal>

            <Modal 
                isOpen={isGameLogModalOpen} 
                onClose={() => closeModal(setGameLogModalOpen)}
                className="w-[80%]! h-[80%]! md:h-[70%]! top-[10%]! md:top-[15%]! left-[10%]! !blur-none z-50"
            >
                <HUDGameEventLog 
                    gameId={gameId}
                    playerColours={playerColours}

                />
            </Modal>

            <Modal 
                isOpen={isGameChatModalOpen} 
                onClose={() => closeModal(setGameChatModalOpen)}
                className="w-[80%]! h-[80%]! md:h-[70%]! top-[10%]! md:top-[15%]! left-[10%]! !blur-none z-50"
            >
                <HUDGameChat 
                    gameId={gameId}
                    userId={userId}
                    playerColours={playerColours}
                    gameMode={gameMode}
                    isMobile={isMobile}
                />
            </Modal>
            
        </div>
    )

}