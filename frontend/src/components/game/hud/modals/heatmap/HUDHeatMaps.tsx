import type { GamePlayers } from "@/types/game/GameTypes"
import { HeatMap } from "./HeatMap"
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils"
import { getPercentageBoardComplete } from "@/utils/game/boardStateUtils"

export function HUDHeatMaps({
    userId,
    gamePlayers,
    isMobile
}: {
    userId: number,
    gamePlayers: GamePlayers,
    isMobile: boolean
}) {
    return (
        <div className="flex flex-col flex-1 bg-background rounded">
            <div className="flex w-full h-auto py-3 bg-sidebar rounded-t justify-between items-center px-5">
                <h3 className="tracking-wider text-2xl font-semibold font-display text-sidebar-foreground">
                    Heat Maps
                </h3>
                <span className="text-muted-foreground font-sans text-md pr-10">
                    Board completion
                </span>
            </div>

            {
                isMobile ? (
                    <div className="flex flex-col w-full h-full overflow-y-auto py-3 items-center gap-3">
                        {Object.entries(gamePlayers).map(([key, gp], index) => {
                            return (
                                <div 
                                    className={`flex h-[50%] w-[90%] p-4 border-2 md:border-3 border-muted rounded-xl flex-col justify-center bg-muted/20 items-center ${userId === Number(key) && "bg-sidebar-primary/20 border-sidebar-primary"}`}
                                    key={index}
                                >
                                    <div 
                                        className="flex w-full justify-between md:px-2 items-center"
                                    >
                                        <div className="flex w-auto items-center justify-center gap-3">
                                            <div 
                                                className={`left-0 p-2 md:p-3 my-1 border-muted border-1 rounded ${playerColourClassNamePicker[gp.colour].medium}`}
                                            >
                                            </div>
                                            <span className="font-semibold text-md whitespace-nowrap overflow-hidden text-ellipsis text-accent-foreground font-display tracking-wide">{ gp.name }</span>
                                        </div>
                                        <div>
                                            <span className="font-extrabold text-primary text-md font-display">
                                                {getPercentageBoardComplete(gp.boardProgress)}%
                                            </span>
                                        </div>
                                    </div>
                                    <HeatMap
                                        playerColour={gp.colour}
                                        boardProgress={gp.boardProgress}
                                        isMobile={isMobile}
                                    />
                                </div>
                            )
                        })}
                    </div>
                ) : (
                    <div className="w-full h-full grid grid-cols-2 p-8 gap-5">
                        {Object.entries(gamePlayers).map(([key, gp], index) => {
                            return (
                                <div 
                                    className={`flex h-[45%] p-4 border-2 md:border-3 border-muted rounded-xl flex-col justify-center bg-muted/20 items-center ${userId === Number(key) && "bg-sidebar-primary/20 border-sidebar-primary"}`}
                                    key={index}
                                >
                                    <div 
                                        className="flex w-full justify-between md:px-2 items-center"
                                    >
                                        <div className="flex w-auto items-center justify-center gap-3">
                                            <div 
                                                className={`left-0 p-2 md:p-3 my-1 border-muted border-1 rounded ${playerColourClassNamePicker[gp.colour].medium}`}
                                            >
                                            </div>
                                            <span className="font-semibold text-md whitespace-nowrap overflow-hidden text-ellipsis text-accent-foreground font-display tracking-wide">{ gp.name }</span>
                                        </div>
                                        <div>
                                            <span className="font-extrabold text-primary text-md font-display">
                                                {getPercentageBoardComplete(gp.boardProgress)}%
                                            </span>
                                        </div>
                                    </div>
                                    <HeatMap
                                        playerColour={gp.colour}
                                        boardProgress={gp.boardProgress}
                                        isMobile={isMobile}
                                    />
                                </div>
                            )
                        })}
                    </div>
                )
            }
            
        </div>
    )
}