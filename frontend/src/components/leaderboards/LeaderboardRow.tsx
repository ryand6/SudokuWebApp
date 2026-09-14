import type { LeaderboardRowDto } from "@/types/dto/entity/leaderboards/LeaderboardRowDto"

export function LeaderboardRow({
    data,
    userId,
    isMobile
}: {
    data: LeaderboardRowDto,
    userId: number,
    isMobile: boolean
}) {
    return (
        <div className="flex w-full justify-between py-2 px-4 border-b-1 border-muted bg-background font-display">
            <div className="flex flex-1 justify-start gap-4">
                <div 
                    className={`flex flex-1 md:max-w-[10%] justify-center font-bold font-display
                                ${data.rank === 1 ? "text-[#DDB84A]" : data.rank === 2 ? "text-[#C0C0C0]" : data.rank === 3 ? "text-[#CD7F32]" : data.userId === userId ? "text-secondary" : "text-muted-foreground"}`}
                >
                    {data.rank.toLocaleString()}
                </div>
                <div 
                    className={`flex flex-1 md:max-w-[10%] justify-center font-bold font-display tracking-wide
                                ${data.userId === userId ? "text-secondary" : "text-foreground"}`}
                >
                    {data.username}
                </div>
            </div>
            <div className="flex flex-1 gap-4">
                <div 
                    className={`flex flex-1 justify-center font-bold font-display tracking-wide
                                ${data.userId === userId ? "text-secondary" : "text-accent-foreground"}`}
                >
                    {data.totalScore.toLocaleString()}
                </div>
                <div className="flex flex-1 justify-center font-semibold text-sidebar-accent-foreground">
                    {data.gamesPlayed.toLocaleString()}
                </div>
                {
                    !isMobile && (
                        <>
                            <div className="flex flex-1 justify-center font-semibold text-secondary">
                                {data.wins.toLocaleString()}
                            </div>
                            <div className="flex flex-1 justify-center font-semibold text-destructive/80">
                                {data.losses.toLocaleString()}
                            </div>
                            <div className="flex flex-1 justify-center font-semibold text-muted-foreground">
                                {data.draws.toLocaleString()}
                            </div>
                            <div className="flex flex-1 justify-center font-semibold text-secondary">
                                {data.maxWinStreak.toLocaleString()}
                            </div>
                        </>
                    )
                }
            </div>
        </div>
    )
}