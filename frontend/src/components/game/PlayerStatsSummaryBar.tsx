import type { GamePlayers } from "@/types/game/GameTypes";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";

export function PlayerStatsSummaryBar({
    userId,
    gamePlayers,
    currentStreak,
}: {
    userId: number,
    gamePlayers: GamePlayers,
    currentStreak: number,
}) {
    return (
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
    )

}