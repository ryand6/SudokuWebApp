import { Button } from "@/components/ui/button";
import { Separator } from "../../ui/separator";

export function PrematureEndBanner({ 
    onContinue 
}: { 
    onContinue: () => void 
}) {
    return (
        <div className="flex flex-col border-b-2 border-border w-full bg-accent px-4 pt-3.5 pb-4 gap-2.5">
            <div className="flex gap-2.5 items-start">
                <span className="text-xl leading-none mt-0.5">🏳️</span>
                <div className="flex flex-col gap-0.5">
                    <span
                        className="text-md font-semibold text-accent-foreground font-display"
                    >
                        All other players forfeited
                    </span>
                    <span
                        className="text-sm text-accent-foreground/70 font-sans"
                    >
                        You've been awarded the win by default.
                    </span>
                </div>
            </div>
            <Separator orientation="horizontal" /> 
            <div className="flex items-center justify-between gap-3">
                <span className="text-sm text-accent-foreground/70 font-sans flex-1">
                    Want to keep going and finish the board at your own pace?
                </span>
                <Button
                    variant="secondary"
                    className="text-sm font-display cursor-pointer"
                    onClick={onContinue}
                >
                    Continue →
                </Button>
            </div>
        </div>
    );
}