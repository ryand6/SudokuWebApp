import type { QueryClient } from "@tanstack/react-query";
import type { GameCreationEvent } from "./lobbyEvents";
import type { NavigateFunction } from "react-router-dom";
import { normalisePublicGameData } from "@/utils/game/normaliseGameState";
import type { PublicGameState } from "@/types/game/GameTypes";
import { queryKeys } from "@/state/queryKeys";
import type { GameDto } from "@/types/dto/entity/game/GameDto";

export function gameCreationCacheDispatcher(
    queryClient: QueryClient,
    navigate: NavigateFunction,
    event: GameCreationEvent
) {
    const gameDto: GameDto = event.gameData;
    const publicGameData: PublicGameState = normalisePublicGameData(gameDto);
    queryClient.setQueryData(queryKeys.game(gameDto.gameId), publicGameData);
    navigate(`/game/${publicGameData.gameId}`);
    return;
}