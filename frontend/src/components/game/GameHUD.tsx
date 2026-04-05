import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import type { GamePlayers } from "@/types/game/GameTypes";
import { HUDStats } from "./HUDStats";
import { Modal } from "../ui/custom/Modal";
import { useState, type Dispatch, type SetStateAction } from "react";
import { playerColourClassNamePicker } from "@/utils/game/cellUtils";

export function GameHUD(
    {
        userId,
        gamePlayers, 
        difficulty, 
        gameMode,
        currentStreak,
    }: {
        userId: number
        gamePlayers: GamePlayers, 
        difficulty: Difficulty, 
        gameMode: GameMode,
        currentStreak: number
    }
) {

    const [isStatsModalOpen, setStatsModalOpen] = useState(false);

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
                        h-auto max-h-[200px]"
        >   
            <div className="flex justify-evenly">
                <div 
                    onClick={() => openModal(setStatsModalOpen)}
                    className="flex justify-center items-center h-full w-[20%] text-lg md:text-xl lg:text-2xl hover:bg-sidebar-primary rounded cursor-pointer elevated"
                >
                    Game Stats    
                </div>
            </div>
            <div className="flex">
                <div className="flex flex-1">
                    {Object.entries(gamePlayers).map(([key, player]) => {
                        return (
                            <div className="flex">
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
                className="w-[60%]! h-[60%]! top-[20%]! left-[20%]! !blur-none z-50"
            >
                <HUDStats 
                    userId={userId}
                    gamePlayers={gamePlayers}
                    currentStreak={currentStreak} 
                />
            </Modal>
            

        </div>
    )

}