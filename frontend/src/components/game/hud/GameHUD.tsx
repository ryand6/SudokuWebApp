import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import type { GamePlayer, GamePlayers } from "@/types/game/GameTypes";
import { Modal } from "../../ui/custom/Modal";
import { useState, type Dispatch, type SetStateAction } from "react";
import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";
import { UserSettings } from "../../shared/UserSettings";
import { QueryClient, type UseMutateFunction } from "@tanstack/react-query";
import { Button } from "../../ui/button";
import { LeaveGameAlertDialog } from "../../ui/custom/LeaveGameAlertDialog";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LeaveGameRequestDto } from "@/types/dto/request/LeaveGameRequestDto";
import type { GameType } from "@/types/enum/GameType";
import { GameInfoBar } from "../info/GameInfoBar";
import { HUDStats } from "./modals/stats/HUDStats";
import { HUDHeatMaps } from "./modals/heatmap/HUDHeatMaps";
import { HUDGameEventLog } from "./modals/log/HUDGameEventLog";
import { HUDGameChat } from "./modals/chat/HUDGameChat";

export function GameHUD(
    {
        userId,
        gameId,
        gamePlayers, 
        difficulty, 
        gameMode,
        gameType,
        currentStreak,
        userSettings,
        gameEndsAt,
        leaveGameHandler,
        queryClient
    }: {
        userId: number,
        gameId: number,
        gamePlayers: GamePlayers, 
        difficulty: Difficulty, 
        gameMode: GameMode,
        gameType: GameType,
        currentStreak: number,
        userSettings: UserSettingsDto,
        gameEndsAt: string | null,
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

    const openModal = (setter: Dispatch<SetStateAction<boolean>>) => {
        setter(true);
        document.getElementById("root")?.classList.add("blur-sm");
    };

    const closeModal = (setter: Dispatch<SetStateAction<boolean>>) => {
        setter(false);
        document.getElementById("root")?.classList.remove("blur-sm");
    };

    const leaveGameComponent = (
        <div className="flex justify-end h-full w-full ">
            <LeaveGameAlertDialog open={isAlertOpen} handleContinueClick={() => leaveGameHandler.mutate({gameId, userId})} setOpen={setIsAlertOpen} />
            <Button variant="destructive" className="cursor-pointer" onClick={() => setIsAlertOpen(true)}>
                Leave Game
            </Button>
        </div>
    ) 

    return (
        <div 
            className="flex flex-col gap-4 bg-card border-border border-2 py-2 rounded-sm
                        h-auto max-h-[200px] w-full"
        >   
            <div className="flex justify-evenly">
                <div 
                    onClick={() => openModal(setStatsModalOpen)}
                    className="flex justify-center items-center h-full w-[20%] text-lg md:text-xl lg:text-2xl hover:bg-sidebar-primary rounded cursor-pointer elevated"
                >
                    Game Stats    
                </div>
                <div 
                    onClick={() => openModal(setHeatMapModalOpen)}
                    className="flex justify-center items-center h-full w-[20%] text-lg md:text-xl lg:text-2xl hover:bg-sidebar-primary rounded cursor-pointer elevated"
                >
                    Heat Maps   
                </div>
                <div 
                    onClick={() => openModal(setGameLogModalOpen)}
                    className="flex justify-center items-center h-full w-[20%] text-lg md:text-xl lg:text-2xl hover:bg-sidebar-primary rounded cursor-pointer elevated"
                >
                    Game Log   
                </div>
                <div 
                    onClick={() => openModal(setGameChatModalOpen)}
                    className="flex justify-center items-center h-full w-[20%] text-lg md:text-xl lg:text-2xl hover:bg-sidebar-primary rounded cursor-pointer elevated"
                >
                    Game Chat   
                </div>
                <UserSettings settings={userSettings} queryClient={queryClient} additionalActions={leaveGameComponent} />
            </div>
            <div>
                <GameInfoBar
                    difficulty={difficulty} 
                    gameMode={gameMode}
                    gameType={gameType}
                    gameEndsAt={gameEndsAt}
                />
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