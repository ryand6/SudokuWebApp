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

export function GameResults({
    userId,
    gameId,
    difficulty,
    gameMode,
    leaderboardResult,
    players,
    queryClient
}: {
    userId: number,
    gameId: number,
    difficulty: Difficulty,
    gameMode: GameMode,
    leaderboardResult: LeaderboardResult | undefined,
    players: GamePlayers,
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

    return (
        <div 
            className="flex flex-col w-full items-start gap-4 overflow-y-scroll"
        >   
                <div
                    className="rounded-t-sm w-full px-5 pt-5 pb-4 text-center bg-sidebar"
                >
                    <p
                        className="text-sm tracking-wider uppercase text-sidebar-primary mb-1 font-display"
                    >
                        Round complete
                    </p>
                    <h1
                        className="text-lg tracking-wide font-semibold text-sidebar-foreground leading-tight m-0 font-display"
                    >
                        Game Results
                    </h1>
                    {/* Hardcoded — replace with real game metadata */}
                    <p
                        className="text-sm text-sidebar-accent mt-1.5 font-sans"
                    >
                        {wordToProperCase(difficulty)} · {wordToProperCase(gameMode)}
                    </p>
                    
                    <StatusPill result={players[userId].gameResult} />

                    {/* <StatusPill result={"PENDING"} /> */}
                </div>           
            <div className="flex flex-row w-full gap-[15%] items-center">
                {
                    Object.entries(players).map(([playerId, player], index) => 
                        (
                            <div className="flex flex-col p-5">
                                <span>{ player.name }</span>
                                {   
                                    player.gameResult === "FORFEIT" ? (
                                        <span>Player forfeitted</span>
                                    ) :
                                        player.finishedGame ? (
                                            <span>{ player.score }</span>
                                        ) : (
                                            <SpinnerButton text="Waiting for player to finish..." />
                                        )
                                }
                            </div>
                        )
                    )
                }
            </div>
            <Separator orientation="horizontal"  />
            <h2>Leaderboard Score</h2>
            <div>
                {leaderboardResult === undefined ? (
                    <SpinnerButton text="Loading leaderboard score..." />
                ): (
                    <div className="flex flex-col">
                        <div className="flex flex-col">
                            <div>
                                <span>{leaderboardResult.score}</span><span>Base score</span>
                            </div>
                            <div>
                                <span>÷ {leaderboardResult.cellsCompleted}</span><span>Cells completed ({leaderboardResult.scoreOverCellsCompleted})</span>
                            </div>
                            <div>
                                <span>x {leaderboardResult.normalisationRate}</span><span>Percentage of cells completed ({leaderboardResult.normalisedScore})</span>
                            </div>
                            <div>
                                <span>x {leaderboardResult.difficultyMultiplier}</span><span>Difficulty multiplier ({leaderboardResult.scoreTimesDifficultyMultiplier})</span>
                            </div>
                            <div>
                                <span>x {leaderboardResult.timerMultiplier}</span><span>Timer multiplier ({leaderboardResult.scoreTimesTimerMultiplier})</span>
                            </div>
                            <Separator orientation="horizontal" />
                        </div>
                        <div>
                            <span>Final Score:</span><span>{leaderboardResult.finalScore}</span>
                        </div>
                    </div>
                )}
                {error && (
                    <div>
                        <span>{error}</span>
                    </div>
                )}
            </div>
            <Button variant="destructive">Return to Lobby</Button>
        </div>
    )
}