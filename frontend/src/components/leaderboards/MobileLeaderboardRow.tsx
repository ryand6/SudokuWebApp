import type { LeaderboardRowDto } from "@/types/dto/entity/leaderboards/LeaderboardRowDto";
import { Accordion, AccordionContent, AccordionItem } from "../ui/accordion";
import { ChevronDown } from "lucide-react";
import { NoChevronAccordionTrigger } from "../ui/custom/NoChevronAccordionTrigger";
import { StatCard } from "../game/results/StatCard";
import type { GameMode } from "@/types/enum/GameMode";

export function MobileLeaderboardRow({
    data,
    userId,
    gameMode
}: {
    data: LeaderboardRowDto,
    userId: number,
    gameMode: GameMode
}) {

    return (
        <Accordion type="single" collapsible>
            <AccordionItem value={userId.toString() + gameMode}>
                <NoChevronAccordionTrigger
                    className="w-full p-0"
                >
                    <div className="flex w-full items-center bg-background font-display border-b-1 border-muted group-data-[state=open]:border-b-0 group-data-[state=open]:bg-sidebar-primary/20">
                        <div className="flex flex-1 justify-start gap-4 py-2 px-4">
                            <div
                                className={`flex flex-1 justify-center font-bold
                                    ${
                                        data.rank === 1
                                            ? "text-[#DDB84A]"
                                            : data.rank === 2
                                                ? "text-[#C0C0C0]"
                                                : data.rank === 3
                                                    ? "text-[#CD7F32]"
                                                    : data.userId === userId
                                                        ? "text-secondary"
                                                        : "text-muted-foreground"
                                    }`}
                            >
                                {data.rank.toLocaleString()}
                            </div>

                            <div
                                className={`flex flex-1 justify-center font-bold tracking-wide
                                        ${data.userId === userId ? "text-secondary" : "text-foreground"}`}
                            >
                                {data.username}
                            </div>
                            <div
                                className={`flex flex-1 justify-center font-bold tracking-wide
                                        ${data.userId === userId ? "text-secondary" : "text-accent-foreground"}`}
                            >
                                {data.totalScore.toLocaleString()}
                            </div>

                            <div className="flex flex-1 justify-center font-semibold text-sidebar-accent-foreground">
                                {data.gamesPlayed.toLocaleString()}
                            </div>
                            <div className="flex flex-1 justify-center">
                                <ChevronDown
                                    className="size-4 transition-transform duration-200 group-data-[state=open]:rotate-180"
                                />
                            </div>
                        </div>
                    </div>
                </NoChevronAccordionTrigger>
                <AccordionContent>
                    <div className="px-4 py-2 bg-sidebar-primary/20 flex justify-center flex-wrap gap-2 border-b-2 border-muted">
                        <StatCard value={data.wins.toLocaleString()} label="Wins" compact={true} />
                        <StatCard value={data.losses.toLocaleString()} label="Losses" compact={true} />
                        <StatCard value={data.draws.toLocaleString()} label="Draws" compact={true} />
                        <StatCard value={Math.round((data.wins / data.gamesPlayed) * 100).toLocaleString() + "%"} label="Win Rate" compact={true} />
                        <StatCard value={data.maxWinStreak.toLocaleString()} label="Max Win Streak" compact={true} />
                    </div>
                </AccordionContent>
            </AccordionItem>
        </Accordion>
    )
}