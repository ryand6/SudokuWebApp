import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import type { GameType } from "@/types/enum/GameType";
import BasicTimer from "@/components/ui/custom/BasicTimer";

export function GameInfoBar({
    difficulty, 
    gameMode,
    gameType,
    gameEndsAt
}: {
    difficulty: Difficulty, 
    gameMode: GameMode,
    gameType: GameType,
    gameEndsAt: string | null
}) {
    return (
        <div className="flex gap-5 justify-center px-5 text-foreground text-lg font-bold">
            <span>{wordToProperCase(difficulty)}</span>
            <span>&middot;</span>
            <span>{wordToProperCase(gameMode)}</span>
            <span>&middot;</span>
            <span>{wordToProperCase(gameType)}</span>
            {
                gameEndsAt && (
                    <>
                        <span>&middot;</span>
                        <BasicTimer endTime={getEpochTimeFromTimestamp(gameEndsAt)} unit="MINUTES" />
                    </>
                )
            }
            
        </div>
    )
}