import { useEffect } from "react";
import type { NavigateFunction } from "react-router-dom";

export function useNavigateUserWhenInGame(
    lobbyInGame: boolean | null | undefined,
    currentGameId: number | null | undefined, 
    userId: number | null | undefined,
    playerInGame: boolean | null,
    gameId: number | null | undefined,
    isLoadingGameData: boolean,
    navigate: NavigateFunction
) {
    useEffect(() => {
        if (!lobbyInGame || !currentGameId || !userId || !playerInGame || !gameId || isLoadingGameData) {
            return;
        }
        navigate(`/game/${gameId}`);
    }, [lobbyInGame, currentGameId, userId, playerInGame, gameId, isLoadingGameData, navigate]);
}