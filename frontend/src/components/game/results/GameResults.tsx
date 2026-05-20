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
import { computeMsDifferenceBetweenTimestamps } from "@/utils/time/timeDifference";
import { convertMillisecondsToMinuteClock } from "@/utils/time/convertMillisecondsToMinuteClock";
import { getUserRank } from "@/api/rest/users/query/getUserRank";
import { PrematureEndBanner } from "./PrematureEndBanner";
import { ReturnToLobbyAlertDialog } from "@/components/ui/custom/ReturnToLobbyAlertDialog";
import { revertInGameStatus } from "@/api/ws/game/playerstate/revertInGameStatus";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import type { NavigateFunction } from "react-router-dom";
import { getEpochTimeFromTimestamp } from "@/utils/time/getEpochTimeFromTimestamp";
import BasicTimer from "@/components/ui/custom/BasicTimer";

export function GameResults({
    userId,
    gameId,
    lobbyId,
    difficulty,
    gameMode,
    leaderboardResult,
    players,
    gameStartsAt,
    endedPrematurely,
    gameEndedAt,
    queryClient,
    navigate
}: {
    userId: number,
    gameId: number,
    lobbyId: number,
    difficulty: Difficulty,
    gameMode: GameMode,
    leaderboardResult: LeaderboardResult | undefined,
    players: GamePlayers,
    gameStartsAt: string | null,
    endedPrematurely: boolean,
    gameEndedAt: string | null,
    queryClient: QueryClient,
    navigate: NavigateFunction
}) {
    const [error, setError] = useState<string | null>(null);
    const [userRankText, setUserRankText] = useState<string>("-");
    const [isAlertOpen, setIsAlertOpen] = useState(false);

    document.getElementById("root")?.classList.add("blur-sm");

    const { send } = useWebSocketContext();

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

    const timeTaken: string = players[userId].finishedGameTimestamp && gameStartsAt ? convertMillisecondsToMinuteClock(computeMsDifferenceBetweenTimestamps(players[userId].finishedGameTimestamp, gameStartsAt)) : "-";

    const resolveUserRank = async () => {
        const userRank = await getUserRank();
        setUserRankText("#" + userRank.userRank.toString());
    }

    const returnToLobbyHandler = () => {
        revertInGameStatus(send, gameId, userId, lobbyId);
        navigate(`/lobby/${lobbyId}`);
    }

    // Implement
    const onContinue = () => {

    }

    resolveUserRank();

    console.log(gameEndedAt);

    return (
        <div 
            className="flex flex-col w-full items-start overflow-y-scroll"
            id="game-result-modal"
        >   
            <div
                className="rounded-t-sm w-full px-5 pt-4 pb-4 text-center bg-sidebar"
            >   
                <div className="mr-3 height-[14px] flex justify-end">
                    {
                        gameEndedAt && 
                        <BasicTimer 
                            endTime={getEpochTimeFromTimestamp(gameEndedAt) + 60000}
                            className="font-sans text-[14px] font-bold"
                            timerEndAction={returnToLobbyHandler}
                        />
                    }
                </div>
                
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
            </div>
            {
                endedPrematurely && <PrematureEndBanner onContinue={onContinue} />
            }
            <div className="flex flex-col gap-0 px-4 py-4 w-full">
                <p
                    className="text-sm tracking-wide uppercase text-muted-foreground mb-2 font-display"
                >
                    Players
                </p>
                <div className="grid grid-cols-2 gap-2 mb-4">
                    {
                        Object.entries(players).map(([playerId, player], index) => (
                            <PlayerCard player={player} key={index} />
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
                    <StatCard value={userRankText} label="Leaderboard rank" />
                </div>
                <ReturnToLobbyAlertDialog open={isAlertOpen} handleContinueClick={() => returnToLobbyHandler()} setOpen={setIsAlertOpen} />
                <Button variant="destructive" className="cursor-pointer" onClick={() => setIsAlertOpen(true)}>Return to Lobby</Button>
            </div>
        </div>
    )
}