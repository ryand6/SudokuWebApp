import { Button } from "@/components/ui/button";
import { Separator } from "../../ui/separator";
import { useState } from "react";
import { CasualModeContinueAlertDialog } from "@/components/ui/custom/CasualModeContinueAlertDialog";

export function PrematureEndBanner({ 
    onContinue 
}: { 
    onContinue: () => void 
}) {
    const [isAlertOpen, setIsAlertOpen] = useState(false);

    return (
        <div className="flex flex-col border-b-2 border-border w-full bg-accent px-4 pt-3.5 pb-4 gap-2.5">
            <div className="flex gap-2.5 items-start">
                <span className="text-xl leading-none mt-0.5">🏳️</span>
                <div className="flex flex-col gap-0.5">
                    <span
                        className="text-lg font-semibold text-accent-foreground font-display"
                    >
                        All other players forfeited
                    </span>
                    <span
                        className="text-md text-accent-foreground/70 font-sans"
                    >
                        You've been awarded the win by default.
                    </span>
                </div>
            </div>
            <Separator orientation="horizontal" /> 
            <div className="flex items-center justify-between gap-3">
                <span className="text-md text-accent-foreground/70 font-sans flex-1">
                    Want to keep going and finish the board at your own pace?
                </span>
                <CasualModeContinueAlertDialog open={isAlertOpen} handleContinueClick={() => onContinue()} setOpen={setIsAlertOpen} />
                <Button
                    variant="secondary"
                    className="text-md font-display cursor-pointer"
                    onClick={() => setIsAlertOpen(true)}
                >
                    Continue →
                </Button>
            </div>
        </div>
    );
}