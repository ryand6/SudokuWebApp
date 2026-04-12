import type { GamePlayers } from "@/types/game/GameTypes"
import { HeatMap } from "./HeatMap"

export function HUDHeatMaps({
    userId,
    gamePlayers
}: {
    userId: number,
    gamePlayers: GamePlayers
}) {
    return (
        <div className="p-4 w-full">
            <div className="flex flex-col flex-1">
                <h2 className="font-bold text-secondary">Heat Maps</h2>
                {Object.values(gamePlayers).map((gp, index) => {
                    return (
                        <div 
                            className="flex justify-center items-center"
                        >
                            <div className="flex flex-col items-center w-full gap-2">
                                <div>
                                    {gp.name}
                                </div>
                                <HeatMap
                                    playerColour={gp.colour}
                                    boardProgress={gp.boardProgress}
                                    key={index}
                                />
                                <div className="horizontal-divider"></div>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}