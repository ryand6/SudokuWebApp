import { getLeaderboardResult } from "@/api/rest/game/query/getLeaderboardResult";
import { gamePlayerStateCacheDispatcher } from "@/state/game/player/gamePlayerStateCacheDispatcher";
import type { GamePlayers, LeaderboardResult } from "@/types/game/GameTypes"
import type { QueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { SpinnerButton } from "../../ui/custom/SpinnerButton";
import { Separator } from "../../ui/separator";
import { Button } from "../../ui/button";
import { StatusPill } from "./StatusPill";
import type { Difficulty } from "@/types/enum/Difficulty";
import type { GameMode } from "@/types/enum/GameMode";
import { wordToProperCase } from "@/utils/string/wordToProperCase";
import { PlayerCard } from "./PlayerCard";
import { LeaderboardScoreBreakdownRow } from "./LeaderboardScoreBreakdownRow";
import { StatCard } from "./StatCard";
import { computeSecondsDifferenceBetweenTimestamps } from "@/utils/time/timeDifference";
import { convertMillisecondsToMinuteClock } from "@/utils/time/convertMillisecondsToMinuteClock";

export function GameResults({
    userId,
    gameId,
    difficulty,
    gameMode,
    leaderboardResult,
    players,
    gameStartsAt,
    queryClient
}: {
    userId: number,
    gameId: number,
    difficulty: Difficulty,
    gameMode: GameMode,
    leaderboardResult: LeaderboardResult | undefined,
    players: GamePlayers,
    gameStartsAt: string | null,
    queryClient: QueryClient
}) {
    const [error, setError] = useState<string | null>(null);

    document.getElementById("root")?.classList.add("blur-sm");

    useEffect(() => {
        if (leaderboardResult !== undefined) return;
        
        const fetchLeaderboard = async () => {
            try {
                const fetchedLeaderboard: LeaderboardResult = await getLeaderboardResult(gameId);
                gamePlayerStateCacheDispatcher(queryClient, gameId, userId, {
                    type: "LEADERBOARD_SCORE_CALCULATED",
                    leaderboardResult: fetchedLeaderboard
                });
            } catch (err) {
                setError("Error fetching leaderboard result. Please try again later.");
            }
        }

        fetchLeaderboard();
    }, [gameId, userId, leaderboardResult]);

    const timeTaken: string = players[userId].finishedGameTimestamp && gameStartsAt ? convertMillisecondsToMinuteClock(computeSecondsDifferenceBetweenTimestamps(players[userId].finishedGameTimestamp, gameStartsAt)) : "-";

    return (
        <div 
            className="flex flex-col w-full items-start gap-4 overflow-y-scroll"
        >   
            <div
                className="rounded-t-sm w-full px-5 pt-5 pb-4 text-center bg-sidebar"
            >
                <p
                    className="text-sm tracking-widest uppercase text-sidebar-primary mb-1 font-display"
                >
                    Round complete
                </p>
                <h1
                    className="text-lg tracking-wide font-semibold text-sidebar-foreground leading-tight m-0 font-display"
                >
                    Game Results
                </h1>
                <p
                    className="text-sm text-sidebar-accent mt-1.5 font-sans"
                >
                    {wordToProperCase(difficulty)} · {wordToProperCase(gameMode)}
                </p>
                
                <StatusPill result={players[userId].gameResult} />

                {/* <StatusPill result={"PENDING"} /> */}
            </div>
            <div className="flex flex-col gap-0 px-4 py-4 w-full">
                <p
                    className="text-sm tracking-wide uppercase text-muted-foreground mb-2 font-display"
                >
                    Players
                </p>
                <div className="grid grid-cols-2 gap-2 mb-4">
                    {
                        Object.entries(players).map(([playerId, player], index) => (
                            <PlayerCard player={player} />
                        ))
                    }
                </div>
                <Separator orientation="horizontal" className="mb-4" />
                <p
                    className="text-sm tracking-wide uppercase text-muted-foreground mb-2 font-display"
                >
                    Leaderboard score
                </p>

                {leaderboardResult === undefined ? (
                    error ? (
                        <div className="flex justify-center mb-4">
                            <span
                                className="bg-destructive-light border-2 border-destructive text-secondary-foreground text-xs font-semibold 
                                            tracking-wider px-2.5 py-1 rounded-full font-display text-center"
                            >
                                {error}
                            </span>
                        </div>
                    ) : (
                        <div className="mb-4">
                            <SpinnerButton text="Loading leaderboard score…" />
                        </div>
                    ) 
                ) : (
                    <div className="rounded-lg border-2 border-muted bg-card overflow-hidden mb-4">
                        <LeaderboardScoreBreakdownRow
                            operator="="
                            label="Base score"
                            value={leaderboardResult.score.toLocaleString()}
                        />
                        <LeaderboardScoreBreakdownRow
                            operator="÷"
                            label="Cells completed"
                            sublabel={`${leaderboardResult.cellsCompleted} cells`}
                            value={String(leaderboardResult.cellsCompleted)}
                            subvalue={`→ ${leaderboardResult.scoreOverCellsCompleted.toLocaleString()}`}
                        />
                        <LeaderboardScoreBreakdownRow
                            operator="x"
                            label="Completion rate"
                            sublabel={`${leaderboardResult.normalisationRate}% of cells to fill`}
                            value={String(leaderboardResult.normalisationRate)}
                            subvalue={`→ ${leaderboardResult.normalisedScore.toLocaleString()}`}
                        />
                        <LeaderboardScoreBreakdownRow
                            operator="x"
                            label="Difficulty multiplier"
                            value={`${leaderboardResult.difficultyMultiplier}×`}
                            subvalue={`→ ${leaderboardResult.scoreTimesDifficultyMultiplier.toLocaleString()}`}
                        />
                        <LeaderboardScoreBreakdownRow
                            operator="x"
                            label="Timer multiplier"
                            value={`${leaderboardResult.timerMultiplier}×`}
                            subvalue={`→ ${leaderboardResult.scoreTimesTimerMultiplier.toLocaleString()}`}
                        />
                        <LeaderboardScoreBreakdownRow
                            operator="★"
                            label="Final score"
                            value={leaderboardResult.finalScore.toLocaleString()}
                            isFinal
                        />
                    </div>
                )}
                <Separator orientation="horizontal" className="mb-4" />
                <p
                    className="text-sm tracking-wide uppercase text-muted-foreground mb-2 font-display"
                >
                    Game stats
                </p>
                <div className="grid grid-cols-2 grid-rows-2 gap-1.5 mb-4">
                    <StatCard value={timeTaken} label="Time taken" />
                    <StatCard value={players[userId].mistakes.toString()} label="Mistakes" />
                    <StatCard value={players[userId].firsts.toString()} label="Firsts" />
                    <StatCard value="#14" label="Leaderboard rank" />
                </div>
                <Button variant="destructive" className="cursor-pointer">Return to Lobby</Button>
            </div>
        </div>
    )
}