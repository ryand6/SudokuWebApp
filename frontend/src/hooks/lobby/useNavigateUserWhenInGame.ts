import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import type { UserDto } from "@/types/dto/entity/user/UserDto";
import type { PublicGameState } from "@/types/game/GameTypes";
import { useEffect } from "react";
import type { NavigateFunction } from "react-router-dom";

export function useNavigateUserWhenInGame(
    lobby: LobbyDto | null | undefined,
    currentUser: UserDto | null | undefined,
    gameQueryData: PublicGameState | undefined,
    isLoadingGameData: boolean,
    navigate: NavigateFunction
) {
    // Handle navigation only when confirmed if user is part of current game
    useEffect(() => {
        // Ensure game is active, created and normalised before redirect
        if (!lobby || !currentUser || !lobby.inGame || !lobby.currentGameId || isLoadingGameData || !gameQueryData || !gameQueryData.gameId) return;

        console.log("NAVIGATE WHEN USER IN GAME - GAME DATA: ", gameQueryData);

        navigate(`/game/${gameQueryData.gameId}`);
    }, [lobby, currentUser, gameQueryData, isLoadingGameData, navigate]);
}