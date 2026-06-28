import { ClassicModeCard } from "@/components/game/modes/ClassicModeCard";
import { DominationModeCard } from "@/components/game/modes/DominationModeCard";
import { TimeAttackModeCard } from "@/components/game/modes/TimeAttackModeCard";
import { IconInfoCircle } from "@tabler/icons-react";

export function GameModesWidget() {
    const iconSize = 24;

    return (
        <div className="flex flex-col border-2 border-muted rounded-lg w-full font-display">
            <div className="flex w-full border-b-2 border-b-muted bg-card gap-2 px-4 py-2 items-center text-accent-foreground rounded-t-lg">
                <span><IconInfoCircle size={iconSize} /></span>
                <span className="font-semibold text-lg">Online Play</span>
            </div>
            <div className="flex w-full h-full p-5 items-center gap-4">
                <ClassicModeCard iconSize={iconSize} />
                <DominationModeCard iconSize={iconSize} />
                <TimeAttackModeCard iconSize={iconSize} />
            </div>
        </div>
    )
}