import type { GamePlayers } from "@/types/game/GameTypes"
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";

export function HUDStats(
    {
        userId,
        gamePlayers,
        currentStreak,
    }: {
        userId: number
        gamePlayers: GamePlayers,
        currentStreak: number
    }
) {
    return (
        <div className="flex flex-col flex-1 p-4">
            {Object.entries(gamePlayers).map(([key, player], index) => {
                return (
                    <div className="flex justify-center items-center" key={index}>
                        <div className="flex flex-col w-full gap-1">
                            <div 
                                className={`flex flex-col
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
                                <div>
                                    Firsts: { player.firsts }
                                </div>
                                <div>
                                    Mistakes: { player.mistakes }
                                </div>
                                <div>
                                    Max Streak: { player.maxStreak }
                                </div>
                                
                            </div>
                            <div className="horizontal-divider"></div>
                        </div>
                        
                        
                    </div>
                    
                );
            })}
        </div>
    )
}