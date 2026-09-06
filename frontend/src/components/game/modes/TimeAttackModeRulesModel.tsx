import { Separator } from "@/components/ui/separator";
import { IconStopwatch } from "@tabler/icons-react";

export function TimeAttackModeRulesModel({
    iconSize
}: {
    iconSize: number
}) {
    return (
        <div id="modal-content" className="flex flex-col w-full h-full bg-background font-display tracking-wide">
            <div className="flex items-center px-4 py-5 bg-secondary justify-between">
                <div className="flex items-center gap-3 text-secondary-foreground text-lg">
                    <span className="p-3 bg-background/20 rounded-2xl"><IconStopwatch size={iconSize} /></span>
                    <span className="font-semibold text-2xl tracking-wider">Time Attack</span>
                </div>
            </div>
            <div className="flex flex-col p-5 gap-4 overflow-auto">
                <div>
                    <p>
                        All players work together on the same board against a shared countdown timer. Every correct answer adds time, whilst every mistake costs it. Run out of time before the board is complete and everyone loses. 
                    </p>
                </div>
                <Separator className="border-1" />
                <div className="flex flex-col gap-4 py-1">
                    <div className="text-xl tracking-widest text-muted-foreground font-semibold">
                        <span>STARTING TIMER</span>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>Easy</span>
                            <span className="text-accent-foreground font-semibold text-xl">60s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>Medium</span>
                            <span className="text-accent-foreground font-semibold text-xl">50s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>Hard</span>
                            <span className="text-accent-foreground font-semibold text-xl">40s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>Extreme</span>
                            <span className="text-accent-foreground font-semibold text-xl">30s</span>
                        </div>
                    </div>
                </div>
                <Separator className="border-1" />
                <div className="flex flex-col gap-4 py-1">
                    <div className="text-xl tracking-widest text-muted-foreground font-semibold">
                        <span>TEAM SIZE BONUS</span>
                    </div>
                    <div>
                        <p>
                            Additional seconds added to base timer on game start
                        </p>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>2 players</span>
                            <span className="text-secondary font-semibold text-xl">+20s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>3 players</span>
                            <span className="text-secondary font-semibold text-xl">+10s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>4 players</span>
                            <span className="text-accent-foreground font-semibold text-xl">+0s</span>
                        </div>
                    </div>
                </div>
                <Separator className="border-1" />
                <div className="flex flex-col gap-4 py-1">
                    <div className="text-xl tracking-widest text-muted-foreground font-semibold">
                        <span>TIME PER CORRECT ANSWER</span>
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="flex justify-between">
                            <span>80%+ cells remaining </span>
                            <span className="text-secondary font-semibold text-xl">+5s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>60–80% remaining</span>
                            <span className="text-secondary font-semibold text-xl">+9s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>40–60% remaining</span>
                            <span className="text-secondary font-semibold text-xl">+7s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>20–40% remaining</span>
                            <span className="text-secondary font-semibold text-xl">+5s</span>
                        </div>
                        <Separator className="border-1 text-muted" />
                        <div className="flex justify-between">
                            <span>0–20% remaining</span>
                            <span className="text-secondary font-semibold text-xl">+3s</span>
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
                            <span>Incorrect answer</span>
                            <span className="text-destructive font-semibold text-xl">-8s</span>
                        </div>
                    </div>
                </div>
                <Separator className="border-1" />
                <div>
                    <p className="italic font-sans">
                        Time Attack is still being refined — some details may change before full release.
                    </p>
                </div>
            </div>
        </div>
    )
}