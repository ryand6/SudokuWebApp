import { Separator } from "@/components/ui/separator";
import { IconCategory } from "@tabler/icons-react";

export function ClassicModeRulesModel({
    iconSize
}: {
    iconSize: number
}) {
    return (
        <div id="modal-content" className="flex flex-col w-full h-full bg-background font-display tracking-wide">
            <div className="flex items-center px-4 py-5 bg-primary justify-between">
                <div className="flex items-center gap-3 text-primary-foreground text-lg">
                    <span className="p-3 bg-background/20 rounded-2xl"><IconCategory size={iconSize} /></span>
                    <span className="font-semibold text-2xl tracking-wider">Classic</span>
                </div>
            </div>
            <div className="flex flex-col p-5 gap-4 overflow-auto">
                <div>
                    <p>
                        Each player solves their own board. Points are awarded based on the order players correctly answer each cell. The faster and more accurate you answer, the more you score. 
                    </p>
                </div>
                <Separator className="border-1" />
                <div className="flex flex-col gap-4 py-1">
                    <div className="text-xl tracking-widest text-muted-foreground font-semibold">
                        <span>POINTS PER CELL</span>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>Had a prior mistake on the cell</span>
                            <span className="text-accent-foreground font-semibold text-xl">1 pt</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>1st correct (First)</span>
                            <span className="text-secondary font-semibold text-xl">4 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>2nd correctly</span>
                            <span className="text-secondary font-semibold text-xl">3 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>3rd correct</span>
                            <span className="text-secondary font-semibold text-xl">2 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>4th correct</span>
                            <span className="text-secondary font-semibold text-xl">1 pt</span>
                        </div>
                    </div>
                </div>
                <Separator className="border-1" />
                <div className="flex flex-col gap-4 py-1">
                    <div className="text-xl tracking-widest text-muted-foreground font-semibold">
                        <span>PENALTIES</span>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>1st incorrect answer (resets on correct answer)</span>
                            <span className="text-destructive font-semibold text-xl">-4 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>2nd consecutive incorrect answer</span>
                            <span className="text-destructive font-semibold text-xl">-5 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>3rd+ consecutive incorrect answer</span>
                            <span className="text-destructive font-semibold text-xl">-6 pts</span>
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
                            Claim Firsts consecutively to earn bonus points. Any other player getting a First on any cell breaks your streak.
                        </p>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>2 Firsts in a row</span>
                            <span className="text-secondary font-semibold text-xl">+1 pt</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>3 Firsts in a row</span>
                            <span className="text-secondary font-semibold text-xl">+2 pts</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>4+ Firsts in a row</span>
                            <span className="text-secondary font-semibold text-xl">+3 pts</span>
                        </div>
                    </div>
                </div>
                <Separator className="border-1" />
                <div>
                    <p className="italic font-sans">
                        When the first player finishes their board, all remaining players' timers are reduced to 1 minute (ranked games only).
                    </p>
                </div>
            </div>
        </div>
    )
}