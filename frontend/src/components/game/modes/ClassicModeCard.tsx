import { IconArrowNarrowRight, IconCategory } from "@tabler/icons-react"

export function ClassicModeCard({
    iconSize
}: {
    iconSize: number
}) {
    return (
        <div className="flex flex-col border-1 border-muted rounded-lg font-display w-full">
            <div className="flex items-center px-4 py-3 bg-primary justify-between rounded-t-lg">
                <div className="flex items-center gap-2 text-primary-foreground text-lg">
                    <span><IconCategory size={iconSize} /></span>
                    <span className="font-semibold">Classic</span>
                </div>
                <div>
                    <div 
                        className="flex items-end justify-center gap-2 rounded-lg border-1 border-primary-foreground px-3 py-1 w-auto
                                    text-primary-foreground cursor-pointer hover:bg-primary-foreground/20 font-semibold"
                    >
                        <span>Rules</span>
                        <span><IconArrowNarrowRight size={iconSize} /></span>
                    </div>
                </div>
            </div>
            <div className="flex items-center justify-center p-5 text-muted-foreground">
                Each player completes their own board. Bonuses awarded to the first to answer each cell correctly without a mistake. Streaks awarded for consecutive first answers.
            </div>
        </div>
    )
}