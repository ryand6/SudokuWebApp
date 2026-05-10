import { getLeaderboardResult } from "@/api/rest/game/query/getLeaderboardResult";
import { gamePlayerStateCacheDispatcher } from "@/state/game/player/gamePlayerStateCacheDispatcher";
import type { GamePlayers, LeaderboardResult } from "@/types/game/GameTypes"
import type { QueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";

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
        <>
            {error && (
                <div>
                    Error: {error}
                </div>
            )}
        </>
    )
}