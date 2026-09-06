import { Separator } from "@/components/ui/separator";
import { IconSwords } from "@tabler/icons-react";

export function DominationModeRulesModel({
    iconSize
}: {
    iconSize: number
}) {
    return (
        <div id="modal-content" className="flex flex-col w-full h-full bg-background font-display tracking-wide">
            <div className="flex items-center px-4 py-5 bg-sidebar justify-between">
                <div className="flex items-center gap-3 text-sidebar-primary text-lg">
                    <span className="p-3 bg-background/20 rounded-2xl"><IconSwords size={iconSize} /></span>
                    <span className="font-semibold text-2xl tracking-wider">Domination</span>
                </div>
            </div>
            <div className="flex flex-col p-5 gap-4 overflow-auto">
                <div>
                    <p>
                        All players share the same board. Be the first to correctly answer a cell to claim it - claimed cells are locked for everyone else. The game ends when all cells are claimed.  
                    </p>
                </div>
                <Separator className="border-1" />
                <div className="flex flex-col gap-4 py-1">
                    <div className="text-xl tracking-widest text-muted-foreground font-semibold">
                        <span>SCORING</span>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>Claim a cell</span>
                            <span className="text-secondary font-semibold text-xl">+10 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>Incorrect answer</span>
                            <span className="text-destructive font-semibold text-xl">-8 pts</span>
                        </div>
                    </div>
                </div>
                <Separator className="border-1" />
                <div className="flex flex-col gap-4 py-1">
                    <div className="text-xl tracking-widest text-muted-foreground font-semibold">
                        <span>STREAK BONUSES</span>
                    </div>
                    <div>
                        <p>
                            Claim cells consecutively without a mistake to build a streak. Any incorrect answer resets it.
                        </p>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>2 claims in a row </span>
                            <span className="text-secondary font-semibold text-xl">+2 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>3 claims in a row</span>
                            <span className="text-secondary font-semibold text-xl">+3 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>4+ claims in a row</span>
                            <span className="text-secondary font-semibold text-xl">+5 pts</span>
                        </div>
                    </div>
                </div>
                <Separator className="border-1" />
                <div>
                    <p className="italic font-sans">
                        Your leaderboard score is based on the cells you personally claimed, not the total cells completed on the board (ranked games only).
                    </p>
                </div>
            </div>
        </div>
    )
}