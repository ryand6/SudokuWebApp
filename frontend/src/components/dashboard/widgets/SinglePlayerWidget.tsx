import { IconUser } from "@tabler/icons-react";
import { PencilRuler } from "lucide-react";

export function SinglePlayerWidget({
    isMobile
}: {
    isMobile: boolean
}) {
    const iconSize: number = isMobile ? 16 : 24;

    return (
        <div className="flex flex-col border-2 border-muted rounded-lg w-full font-display">
            <div className="flex w-full border-b-2 border-b-muted bg-card gap-2 px-4 py-2 items-center text-accent-foreground rounded-t-lg">
                <span><IconUser size={iconSize} /></span>
                <span className="font-semibold text-lg">Single Player</span>
            </div>
            <div className="flex flex-col items-center gap-4 py-6">
                <div className="border-2 border-dashed border-muted text-muted-foreground bg-muted/30 rounded-full p-3">
                    <PencilRuler size={iconSize} />
                </div>
                <div className="font-display text-xl text-muted-foreground font-semibold">
                    In Development
                </div>
                <div className="flex justify-center font-display text-muted-foreground min-w-[50%] max-w-[75%] text-sm">
                    Solo play is on the way. Check back in soon.
                </div>
            </div>
        </div>
    )
}