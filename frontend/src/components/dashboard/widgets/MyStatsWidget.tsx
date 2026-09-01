import { useGetUserGameModeStats } from "@/api/rest/leaderboards/query/useGetUserGameModeStats";
import { StatCard } from "@/components/game/results/StatCard";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
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

    const { data, isLoading } = useGetUserGameModeStats(selectedMode);

    console.log("Game Mode Stats: ", data);

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
                    <div className="flex flex-col items-center justify-center h-full gap-4 py-3">
                        {
                            isLoading ? (
                                <div>
                                    <SpinnerButton />
                                </div>
                            ) : !data? (
                                <div className="font-display tracking-wide">
                                    <p>No stats available for this game mode</p>
                                </div>
                            ) : (
                                <div className="px-4 flex justify-center flex-wrap gap-2">
                                    <StatCard value={data.gamesPlayed.toLocaleString()} label="Games" />
                                    <StatCard value={data.wins.toLocaleString()} label="Wins" />
                                    <StatCard value={data.losses.toLocaleString()} label="Losses" />
                                    <StatCard value={data.draws.toLocaleString()} label="Draws" />
                                    <StatCard value={data.totalScore.toLocaleString()} label="Total Score" />
                                    <StatCard value={Math.round((data.wins / data.gamesPlayed) * 100).toLocaleString() + "%"} label="Win Rate" />
                                    <StatCard value={data.currentWinStreak.toLocaleString()} label="Current Win Streak" />
                                    <StatCard value={data.maxWinStreak.toLocaleString()} label="Max Win Streak" />
                                </div>
                            )
                        }
                    </div>
                </div>
    )
}