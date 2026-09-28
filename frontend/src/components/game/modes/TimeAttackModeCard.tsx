import { IconArrowNarrowRight, IconStopwatch } from "@tabler/icons-react"
import { useState } from "react";
import { TimeAttackModeRulesModel } from "./TimeAttackModeRulesModel";
import { Modal } from "@/components/ui/custom/Modal";

export function TimeAttackModeCard({
    iconSize
}: {
    iconSize: number
}) {
    const [isRulesModelOpen, setRulesModalOpen] = useState(false);
    
    return (
        <>
            <div className="flex flex-col border-1 border-muted rounded-lg font-display w-full">
                <div className="flex items-center px-4 py-3 bg-time-attack-game-mode justify-between rounded-t-lg">
                    <div className="flex items-center gap-2 text-time-attack-game-mode-foreground text-lg">
                        <span><IconStopwatch size={iconSize} /></span>
                        <span className="font-semibold">Time Attack</span>
                    </div>
                    <div>
                        <div 
                            className="flex items-end justify-center gap-2 rounded-lg border-1 border-time-attack-game-mode-foreground px-3 py-1 w-auto
                                        text-time-attack-game-mode-foreground cursor-pointer hover:bg-time-attack-game-mode-foreground/20 font-semibold"
                            onClick={() => setRulesModalOpen(true)}
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
            <Modal className="max-w-[650px]" isOpen={isRulesModelOpen} onClose={() => setRulesModalOpen(false)}><TimeAttackModeRulesModel iconSize={iconSize} /></Modal>
        </>
        
    )
}