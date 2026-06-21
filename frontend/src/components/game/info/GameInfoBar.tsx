import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import type { GameType } from "@/types/enum/GameType";
import BasicTimer from "@/components/ui/custom/BasicTimer";
import { GameNotificationLayer } from "../notifications/GameNotificationLayer";

export function GameInfoBar({
    difficulty, 
    gameMode,
    gameType,
    gameEndsAt,
    scoreNotificationsEnabled,
    streakNotificationsEnabled
}: {
    difficulty: Difficulty, 
    gameMode: GameMode,
    gameType: GameType,
    gameEndsAt: string | null,
    scoreNotificationsEnabled: boolean,
    streakNotificationsEnabled: boolean
}) {
    return (
        <div 
            className="grid grid-cols-[1fr_auto_1fr] h-auto py-3 w-full bg-sidebar text-sidebar-accent justify-center 
                        items-center md:px-5 font-bold text-md md:text-lg"
        >
            <div />

            <div className="flex items-center gap-2 md:gap-3 justify-self-center">
                <span>{wordToProperCase(difficulty)}</span>
                <span>&middot;</span>
                <span>{wordToProperCase(gameMode)}</span>
                <span>&middot;</span>
                <span>{wordToProperCase(gameType)}</span>
                {
                    gameEndsAt && (
                        <>
                            <span>&middot;</span>
                            <span className="px-1 md:px-2 py-1 border-muted border-1 md:border-2 rounded-lg bg-muted/10 font-mono text-md md:text-lg">
                                <BasicTimer endTime={getEpochTimeFromTimestamp(gameEndsAt)} unit="MINUTES" />
                            </span>
                        </>
                    )
                }
            </div>
            
            <div className="pl-1 flex justify-center items-center">
                <GameNotificationLayer 
                    scoreNotificationsEnabled={scoreNotificationsEnabled}
                    streakNotificationsEnabled={streakNotificationsEnabled}
                />
            </div>    
        </div>
    )
}