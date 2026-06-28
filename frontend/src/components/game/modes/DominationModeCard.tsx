import { IconArrowNarrowRight, IconSwords } from "@tabler/icons-react"

export function DominationModeCard({
    iconSize
}: {
    iconSize: number
}) {
    return (
        <div className="flex flex-col border-1 border-muted rounded-lg font-display w-full">
            <div className="flex items-center px-4 py-3 bg-sidebar justify-between rounded-t-lg">
                <div className="flex items-center gap-2 text-sidebar-primary text-lg">
                    <span><IconSwords size={iconSize} /></span>
                    <span className="font-semibold">Domination</span>
                </div>
                <div>
                    <div 
                        className="flex items-end justify-center gap-2 rounded-lg border-1 border-text-sidebar-primary px-3 py-1 w-auto
                                    text-sidebar-primary cursor-pointer hover:bg-sidebar-primary/20 font-semibold"
                    >
                        <span>Rules</span>
                        <span><IconArrowNarrowRight size={iconSize} /></span>
                    </div>
                </div>
            </div>
            <div className="flex items-center justify-center p-5 text-muted-foreground">
                All players share one board. Territory is the aim of the game, claim as many cells as you can before your opponents. Mistakes here are costly.
            </div>
        </div>
    )
}