import { ScrollArea } from "@/components/ui/scroll-area";
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
        <div className="flex flex-col flex-1 bg-background rounded">
            <div className="flex w-full h-auto py-3 bg-sidebar rounded-t">
                <h3 className="pl-5 tracking-wider text-2xl font-extrabold font-display text-sidebar-foreground">
                    Game Stats
                </h3>
            </div>
            <ScrollArea className="flex-1 min-h-0">
                <div className="flex flex-col gap-3 px-5 py-5 items-center">
                    {Object.entries(gamePlayers).map(([key, player], index) => {
                        return (
                            <div className={`flex flex-col w-full bg-muted/20 border-1 md:border-2 rounded-md border-muted justify-center items-center ${userId === Number(key) && "border-sidebar-primary bg-sidebar-primary/20"}`} key={index}>
                                <div className="flex flex-col w-full gap-1">
                                    <div 
                                        className="flex w-full justify-between px-5 py-3 items-center"
                                    >
                                        <div className="flex w-auto items-center justify-center gap-3">
                                            <div 
                                                className={`left-0 p-3 my-1 border-muted border-1 rounded ${playerColourClassNamePicker[player.colour].medium}`}
                                            >
                                            </div>
                                            <span className="font-semibold text-xl overflow-ellipsis text-accent-foreground font-display tracking-wide">{ player.name }</span>
                                        </div>
                                        { (userId === Number(key)) && currentStreak > 1 && 
                                            (<div>
                                                <span className="font-extrabold text-primary text-md font-display">x{ currentStreak } streak</span>
                                            </div>)
                                        }
                                    </div>
                                    <div className="grid grid-cols-2">
                                        <div className="flex flex-col justify-start py-4 px-6 border-t-1 border-r-1 md:border-t-2 md:border-r-2 border-muted">
                                            <span className="font-display font-semibold text-primary text-4xl">
                                                { player.score.toLocaleString() }
                                            </span>
                                            <span className="text-muted-foreground font-sans text-md">
                                                Score
                                            </span>
                                        </div>
                                        <div className="flex flex-col justify-start py-4 px-6 border-t-1 md:border-t-2 border-muted">
                                            <span className="font-display font-semibold text-primary text-4xl">
                                                { player.firsts }
                                            </span>
                                            <span className="text-muted-foreground font-sans text-md">
                                                Firsts
                                            </span>
                                        </div>
                                        <div className="flex flex-col justify-start py-4 px-6 border-t-1 border-r-1 md:border-t-2 md:border-r-2 border-muted">
                                            <span className="font-display font-semibold text-primary text-4xl">
                                                { player.mistakes }
                                            </span>
                                            <span className="text-muted-foreground font-sans text-md">
                                                Mistakes
                                            </span>
                                        </div>
                                        <div className="flex flex-col justify-start py-4 px-6 border-t-1 md:border-t-2 border-muted">
                                            <span className="font-display font-semibold text-primary text-4xl">
                                                { player.maxStreak }
                                            </span>
                                            <span className="text-muted-foreground font-sans text-md">
                                                Max Streak
                                            </span>
                                        </div>
                                    </div>
                                </div> 
                            </div>
                            
                        );
                    })}
                </div>
            </ScrollArea>
        </div>
    )
}