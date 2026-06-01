import { Separator } from "@/components/ui/separator";
import type { GamePlayers } from "@/types/game/GameTypes";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";

export function PlayerStatsSummaryBar({
    userId,
    gamePlayers,
    currentStreak,
    isMobile
}: {
    userId: number,
    gamePlayers: GamePlayers,
    currentStreak: number,
    isMobile: boolean
}) {

    console.log("isMobile: ", isMobile);

    return (
        <div className="flex md:flex-col md:gap-5 h-[10%] md:h-[50%] w-full border-muted border-y-2 md:border-0">
            {
                !isMobile && (
                    <div className="justify-start">
                        <span className="font-display text-muted-foreground font-semibold tracking-widest text-2xl">
                            PLAYERS
                        </span>
                    </div>
                )
            }
            {Object.entries(gamePlayers).map(([key, player], index) => {
                return (
                    <div className="relative flex w-full md:h-[15%] md:border-muted md:rounded-md md:border-2" key={index}>
                        {
                            isMobile ? (
                                <>
                                    { (userId === Number(key)) && currentStreak > 1 && 
                                        (<div className="absolute top-[5%] right-[25%] text-sm font-extrabold text-primary">x{ currentStreak }</div>)
                                    }
                                    <div 
                                        className={`flex w-full flex-col items-center justify-center
                                                    ${userId === Number(key) && "bg-sidebar-primary/20 "}`}
                                    >   
                                        <div className="flex w-auto items-center justify-center gap-2">
                                            <div 
                                                className={`left-0 p-2 my-1 border-muted border-1 rounded ${playerColourClassNamePicker[player.colour].medium}`}
                                            >
                                            </div>
                                            <span className="font-semibold text-md">{ player.name }</span>
                                            <div className="p-2 my-1 border-1 opacity-0 pointer-events-none" />
                                        </div>
                                        <span className="font-bold text-primary text-md">{ player.score }</span>
                                        <span className="text-xs text-muted-foreground">pts</span>
                                    </div>
                                    {
                                        index !== Object.keys(gamePlayers).length - 1 && (
                                            <Separator orientation="vertical" className="bg-muted px-[1px]" />
                                        )
                                    }
                                </>
                            ) : (
                                <>
                                    { (userId === Number(key)) && currentStreak > 1 && 
                                        (<div className="absolute top-[2%] right-[2%] text-sm font-extrabold text-primary">x{ currentStreak }</div>)
                                    }
                                    <div 
                                        className={`flex w-full items-center justify-between pr-6 pl-3
                                                    ${userId === Number(key) && "bg-sidebar-primary/20 "}`}
                                    >   
                                        <div className="flex w-auto items-center justify-center gap-2">
                                            <div 
                                                className={`left-0 p-3 my-1 border-muted border-1 rounded ${playerColourClassNamePicker[player.colour].medium}`}
                                            >
                                            </div>
                                            <span className="font-semibold text-lg overflow-ellipsis">{ player.name }</span>
                                        </div>
                                        <div>
                                            <span className="font-extrabold text-primary text-xl">{ player.score }</span>
                                        </div>
                                    </div>
                                </>
                            )
                        }
                        
                    </div>
                );
            })}     
        </div>
    )

}