import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import type { GamePlayer, GamePlayers } from "@/types/game/GameTypes";
import { HUDStats } from "./HUDStats";
import { Modal } from "../ui/custom/Modal";
import { useState, type Dispatch, type SetStateAction } from "react";
import { HUDHeatMaps } from "./HUDHeatMaps";
import { HUDGameEventLog } from "./HUDGameEventLog";
import { HUDGameChat } from "./HUDGameChat";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";
import { UserSettings } from "../shared/UserSettings";
import { QueryClient } from "@tanstack/react-query";

export function GameHUD(
    {
        userId,
        gameId,
        gamePlayers, 
        difficulty, 
        gameMode,
        currentStreak,
        userSettings,
        queryClient
    }: {
        userId: number,
        gameId: number,
        gamePlayers: GamePlayers, 
        difficulty: Difficulty, 
        gameMode: GameMode,
        currentStreak: number,
        userSettings: UserSettingsDto,
        queryClient: QueryClient
    }
) {

    const [isStatsModalOpen, setStatsModalOpen] = useState(false);
    const [isHeatMapModalOpen, setHeatMapModalOpen] = useState(false);
    const [isGameLogModalOpen, setGameLogModalOpen] = useState(false);
    const [isGameChatModalOpen, setGameChatModalOpen] = useState(false);

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
                <UserSettings settings={userSettings} queryClient={queryClient} />
            </div>
            <div className="flex">
                <div className="flex flex-1">
                    {Object.entries(gamePlayers).map(([key, player], index) => {
                        return (
                            <div className="flex" key={index}>
                                <div 
                                    className={`flex flex-col px-2 mx-2
                                                ${userId === Number(key) && "elevated shine "}`}
                                >
                                    <div className="flex gap-2">
                                        <div>
                                            { player.name }
                                        </div>
                                        <div className={`p-2 my-1 border-border border-1 ${playerColourClassNamePicker[player.colour].medium}`}></div>
                                        { (userId === Number(key)) && currentStreak > 1 && 
                                            (<div className="font-extrabold">x{ currentStreak }</div>)
                                        }
                                    </div>
                                    <div>
                                        Score: { player.score }
                                    </div>
                                </div>
                                <div className="vertical-divider"></div>
                            </div>
                        );
                    })}     
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