import type { GameStatus } from "@/types/enum/GameStatus";
import { useEffect } from "react";
import type { NavigateFunction } from "react-router-dom";

export function useHandleClosedGame(
    gameStatus: GameStatus | undefined,
    lobbyId: number | undefined,
    navigate: NavigateFunction
) {
    useEffect(() => {
        if (lobbyId && (gameStatus === "CLOSED" || gameStatus === "ABORTED")) {
            navigate(`/lobby/${lobbyId}`);
        }
    }, [gameStatus, lobbyId, navigate]);
}