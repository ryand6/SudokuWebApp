import { IconArrowNarrowRight, IconStopwatch } from "@tabler/icons-react"

export function TimeAttackModeCard({
    iconSize
}: {
    iconSize: number
}) {
    return (
        <div className="flex flex-col border-1 border-muted rounded-lg font-display w-full">
            <div className="flex items-center px-4 py-3 bg-secondary justify-between rounded-t-lg">
                <div className="flex items-center gap-2 text-secondary-foreground text-lg">
                    <span><IconStopwatch size={iconSize} /></span>
                    <span className="font-semibold">Time Attack</span>
                </div>
                <div>
                    <div 
                        className="flex items-end justify-center gap-2 rounded-lg border-1 border-secondary-foreground px-3 py-1 w-auto
                                    text-secondary-foreground cursor-pointer hover:bg-secondary-foreground/20 font-semibold"
                    >
                        <span>Rules</span>
                        <span><IconArrowNarrowRight size={iconSize} /></span>
                    </div>
                </div>
            </div>
            <div className="flex items-center justify-center p-5 text-muted-foreground">
                Work together to complete the shared board before the clock runs out. Successful answers add time to the clock, mistakes take time away. Co-operative, time is the enemy.
            </div>
        </div>
    )
}