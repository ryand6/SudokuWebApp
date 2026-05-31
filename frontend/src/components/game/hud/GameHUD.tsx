import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import type { GamePlayers } from "@/types/game/GameTypes";
import { Modal } from "../../ui/custom/Modal";
import { useState, type Dispatch, type SetStateAction } from "react";
import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";
import { QueryClient, type UseMutateFunction } from "@tanstack/react-query";
import { Button } from "../../ui/button";
import { LeaveGameAlertDialog } from "../../ui/custom/LeaveGameAlertDialog";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LeaveGameRequestDto } from "@/types/dto/request/LeaveGameRequestDto";
import type { GameType } from "@/types/enum/GameType";
import { HUDStats } from "./modals/stats/HUDStats";
import { HUDHeatMaps } from "./modals/heatmap/HUDHeatMaps";
import { HUDGameEventLog } from "./modals/log/HUDGameEventLog";
import { HUDGameChat } from "./modals/chat/HUDGameChat";
import { IconChartBar } from '@tabler/icons-react';
import { IconFlame } from '@tabler/icons-react';
import { IconLogs } from '@tabler/icons-react';
import { IconMessageCircle } from '@tabler/icons-react';
import { IconDoorExit } from '@tabler/icons-react';

export function GameHUD(
    {
        userId,
        gameId,
        gamePlayers, 
        currentStreak,
        leaveGameHandler,
        isMobile,
        queryClient
    }: {
        userId: number,
        gameId: number,
        gamePlayers: GamePlayers, 
        currentStreak: number,
        isMobile: boolean,
        leaveGameHandler: {
            mutate: UseMutateFunction<GameDto | null, Error, LeaveGameRequestDto, unknown>;
            isLeaving: boolean;
        },
        queryClient: QueryClient
    }
) {
    const [isStatsModalOpen, setStatsModalOpen] = useState(false);
    const [isHeatMapModalOpen, setHeatMapModalOpen] = useState(false);
    const [isGameLogModalOpen, setGameLogModalOpen] = useState(false);
    const [isGameChatModalOpen, setGameChatModalOpen] = useState(false);
    const [isAlertOpen, setIsAlertOpen] = useState(false);

    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

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
            className="border-border border-2 py-2 h-[10%] w-full"
        >   
            <div className="flex justify-evenly h-full gap-2 px-2">
                <div 
                    onClick={() => openModal(setStatsModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 text-accent-foreground text-md md:text-xl lg:text-2xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary"
                >
                    <span><IconChartBar size={iconSize} stroke={iconStroke} /></span>
                    <span>Stats</span>   
                </div>
                <div 
                    onClick={() => openModal(setHeatMapModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 text-accent-foreground text-sm md:text-xl lg:text-2xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary"
                >
                    <span><IconFlame size={iconSize} stroke={iconStroke} /></span>
                    <span>Heatmaps</span>   
                </div>
                <div 
                    onClick={() => openModal(setGameLogModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 text-accent-foreground text-md md:text-xl lg:text-2xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary"
                >
                    <span><IconLogs size={iconSize} stroke={iconStroke} /></span>
                    <span>Log</span>  
                </div>
                <div 
                    onClick={() => openModal(setGameChatModalOpen)}
                    className="flex justify-center gap-1 items-center h-full w-full bg-background border-muted font-display font-medium
                                border-1 text-accent-foreground text-md md:text-xl lg:text-2xl rounded-md cursor-pointer
                                hover:border-primary hover:bg-primary/10 hover:text-primary"
                >
                    <span><IconMessageCircle size={iconSize} stroke={iconStroke} /></span>
                    <span>Chat</span>
                </div>
                <LeaveGameAlertDialog open={isAlertOpen} handleContinueClick={() => leaveGameHandler.mutate({gameId, userId})} setOpen={setIsAlertOpen} />
                <div className="flex items-center justify-center w-full rounded-md border-1
                            bg-destructive/10 border-destructive/50 text-destructive cursor-pointer
                            hover:border-primary-foreground hover:bg-destructive/50 hover:text-primary-foreground" 
                            onClick={() => setIsAlertOpen(true)}>
                    <IconDoorExit size={iconSize} />
                </div>
        
            </div>

            <Modal 
                isOpen={isStatsModalOpen} 
                onClose={() => closeModal(setStatsModalOpen)}
                className="w-[60%]! h-[75%]! md:h-[60%]! top-[20%]! left-[20%]! !blur-none z-50"
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
                className="w-[60%]! h-[75%]! md:h-[60%]! top-[20%]! left-[20%]! !blur-none z-50 overflow-scroll"
            >
                <HUDHeatMaps 
                    userId={userId}
                    gamePlayers={gamePlayers}
                />
            </Modal>

            <Modal 
                isOpen={isGameLogModalOpen} 
                onClose={() => closeModal(setGameLogModalOpen)}
                className="w-[60%]! h-[75%]! md:h-[60%]! top-[20%]! left-[20%]! !blur-none z-50 overflow-scroll"
            >
                <HUDGameEventLog 
                    gameId={gameId}
                />
            </Modal>

            <Modal 
                isOpen={isGameChatModalOpen} 
                onClose={() => closeModal(setGameChatModalOpen)}
                className="w-[60%]! h-[75%]! md:h-[60%]! top-[20%]! left-[20%]! !blur-none z-50 overflow-scroll"
            >
                <HUDGameChat 
                    gameId={gameId}
                    userId={userId}
                />
            </Modal>
            
        </div>
    )

}