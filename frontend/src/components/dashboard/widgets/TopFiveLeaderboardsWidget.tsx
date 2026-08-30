import { useGetTopFiveWithUserRank } from "@/api/rest/leaderboards/query/useGetTopFiveWithUserRank";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { gameModes, type GameMode } from "@/types/enum/GameMode";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { IconTrophy } from "@tabler/icons-react";
import { useState } from "react";

export function TopFiveLeaderboardsWidget({
    userId,
    isMobile
}: {
    userId: number,
    isMobile: boolean
}) {
    const iconSize: number = isMobile ? 16 : 24;

    const [selectedMode, setSelectedMode] = useState<GameMode>("CLASSIC");

    const { data, isLoading } = useGetTopFiveWithUserRank(selectedMode);

    console.log("Top Five Data: ", data);

    return (
        <div className="flex flex-col border-2 border-muted rounded-lg w-full font-display flex-1">
            <div className="flex w-full border-b-2 border-b-muted bg-card gap-2 px-4 py-2 items-center text-accent-foreground rounded-t-lg">
                <span><IconTrophy size={iconSize} /></span>
                <span className="font-semibold text-lg">Leaderboards</span>
            </div>
            <div className="flex gap-3 px-4 py-2">
                {
                    gameModes.map((mode, index) => (
                        <div 
                            key={index}
                            className={`inline-flex items-center justify-center w-full py-1 rounded-full 
                            font-display border-2 border-muted cursor-pointer
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
                    ) : data?.length === 0 ? (
                        <div className="font-display tracking-wide">
                            <p>No leaderboard data available</p>
                        </div>
                    ) : (
                        <div className="px-5 flex flex-col w-full gap-1">
                            {
                                data?.map((row, index) => (
                                    <div 
                                        key={index}
                                        className={`flex items-center justify-between pb-3 border-b-2 border-b-muted
                                            ${index !== 0 && "py-2"}
                                            ${index > 9 && "border-t-2 border-t-muted py-3"}`}
                                    >
                                        <div className="flex gap-8">
                                            <div
                                                className={`font-bold font-display
                                                    ${row.rank === 1 ? "text-[#DDB84A]" : row.rank === 2 ? "text-[#C0C0C0]" : row.rank === 3 ? "text-[#CD7F32]" : row.userId === userId ? "text-secondary" : "text-muted"}`}
                                            >
                                                {row.rank}
                                            </div>
                                            <div
                                                className={`font-bold font-display tracking-wide
                                                    ${row.userId === userId ? "text-secondary" : "text-foreground"}`}
                                            >
                                                {row.username}
                                            </div>
                                        </div>
                                        <div
                                            className={`font-bold font-display tracking-wide
                                                    ${row.userId === userId ? "text-secondary" : "text-accent-foreground"}`}
                                        >
                                            {row.totalScore}
                                        </div>
                                    </div>
                                ))
                            }
                        </div>
                        
                    )
                }
            </div>
        </div>
    )
}