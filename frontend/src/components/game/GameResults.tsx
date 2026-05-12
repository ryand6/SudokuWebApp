import { getLeaderboardResult } from "@/api/rest/game/query/getLeaderboardResult";
import { gamePlayerStateCacheDispatcher } from "@/state/game/player/gamePlayerStateCacheDispatcher";
import type { GamePlayers, LeaderboardResult } from "@/types/game/GameTypes"
import type { QueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { SpinnerButton } from "../ui/custom/SpinnerButton";
import { Separator } from "../ui/separator";
import { Button } from "../ui/button";

export function GameResults({
    userId,
    gameId,
    leaderboardResult,
    players,
    queryClient
}: {
    userId: number,
    gameId: number,
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
            className="flex flex-col p-5 w-full items-start gap-4"
        >
            <h1>Game Results</h1>
            <Separator orientation="horizontal"  />
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