import { useGetTopTenWithUserRank } from "@/api/rest/leaderboards/query/useGetTopTenWithUserRank";
import type { GameMode } from "@/types/enum/GameMode";
import { IconTrophy } from "@tabler/icons-react";
import { useState } from "react";

export function TopTenLeaderboardsWidget({
    isMobile
}: {
    isMobile: boolean
}) {
    const iconSize: number = isMobile ? 16 : 24;

    const [selectedMode, setSelectedMode] = useState<GameMode>("CLASSIC");

    const { data } = useGetTopTenWithUserRank(selectedMode);

    console.log("Top Ten Data: ", data);

    return (
        <div className="flex flex-col border-2 border-muted rounded-lg w-full font-display">
            <div className="flex w-full border-b-2 border-b-muted bg-card gap-2 px-4 py-2 items-center text-accent-foreground rounded-t-lg">
                <span><IconTrophy size={iconSize} /></span>
                <span className="font-semibold text-lg">Leaderboards</span>
            </div>
            <div className="flex flex-col items-center gap-4 py-6">
                
            </div>
        </div>
    )
}