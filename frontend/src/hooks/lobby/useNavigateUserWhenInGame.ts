import type { GameDto } from "@/types/dto/entity/GameDto";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import type { UseQueryResult } from "@tanstack/react-query";
import { useEffect } from "react";
import type { NavigateFunction } from "react-router-dom";

export function useNavigateUserWhenInGame(
    lobby: LobbyDto | null | undefined,
    currentUser: UserDto | null | undefined,
    gameQuery: UseQueryResult<GameDto, Error>,
    navigate: NavigateFunction
) {
    // Handle navigation only when confirmed if user is part of current game
    useEffect(() => {
        if (!lobby || !currentUser || !lobby.inGame || !lobby.currentGameId) return;

        if (gameQuery.data) {
            navigate(`/game/${gameQuery.data.id}`);
        }
    }, [lobby, currentUser, gameQuery.data, navigate]);
}