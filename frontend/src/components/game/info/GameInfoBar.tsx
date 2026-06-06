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
            className="flex h-auto py-3 w-full bg-sidebar text-sidebar-accent gap-3 justify-center 
                        items-center px-5 font-bold text-xl"
        >
            <div className="w-40"></div>
            <span>{wordToProperCase(difficulty)}</span>
            <span>&middot;</span>
            <span>{wordToProperCase(gameMode)}</span>
            <span>&middot;</span>
            <span>{wordToProperCase(gameType)}</span>
            {
                gameEndsAt && (
                    <>
                        <span>&middot;</span>
                        <span className="px-2 py-1 border-muted border-2 rounded-lg bg-muted/10 font-mono text-xl">
                            <BasicTimer endTime={getEpochTimeFromTimestamp(gameEndsAt)} unit="MINUTES" />
                        </span>
                    </>
                )
            }
            <div className="pl-1 flex justify-center items-center w-40">
                <GameNotificationLayer 
                    scoreNotificationsEnabled={scoreNotificationsEnabled}
                    streakNotificationsEnabled={streakNotificationsEnabled}
                />
            </div>    
        </div>
    )
}