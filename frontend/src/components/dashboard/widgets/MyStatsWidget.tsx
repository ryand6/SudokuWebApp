import { gameModes, type GameMode } from "@/types/enum/GameMode";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { IconChartBar } from "@tabler/icons-react";
import { useState } from "react";

export function MyStatsWidget({
    userId,
    isMobile
}: {
    userId: number,
    isMobile: boolean
}) {

    const iconSize: number = isMobile ? 16 : 24;
    
    const [selectedMode, setSelectedMode] = useState<GameMode>("CLASSIC");

    return (
        <div className="flex flex-col border-2 border-muted rounded-lg w-full font-display flex-1">
                    <div className="flex w-full border-b-2 border-b-muted bg-card gap-2 px-4 py-2 items-center text-accent-foreground rounded-t-lg">
                        <span><IconChartBar size={iconSize} /></span>
                        <span className="font-semibold text-lg">My Stats</span>
                    </div>
                    <div className="flex gap-3 py-2 px-4">
                        {
                            gameModes.map((mode, index) => (
                                <div 
                                    key={index}
                                    className={`inline-flex items-center justify-center gap-2 w-full rounded-full 
                                    py-1 font-display border-2 border-muted cursor-pointer
                                    ${selectedMode === mode ? "bg-secondary text-secondary-foreground border-secondary" : "bg-card text-muted"}`}
                                    onClick={() => setSelectedMode(mode)}
                                >
                                    <span className="text-md text-center font-medium tracking-wide">{wordToProperCase(mode)}</span>
                                </div>
                            ))
                        }
                    </div>
                    <div className="flex flex-col items-center gap-4 py-6">
                    
                    </div>
                </div>
    )
}