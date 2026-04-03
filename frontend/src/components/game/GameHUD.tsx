import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import type { GamePlayers } from "@/types/game/GameTypes";
import { HUDStats } from "./HUDStats";

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

    return (
        <div className="flex gap-4 bg-card border-border border-2 py-2 rounded-sm">
            
            <HUDStats 
                userId={userId}
                gamePlayers={gamePlayers}
                currentStreak={currentStreak} 
            />

            <div className="flex flex-1">
            </div>
            

        </div>
    )

}