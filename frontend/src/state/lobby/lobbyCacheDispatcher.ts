import type { QueryClient } from "@tanstack/react-query";
import type { LobbyEvent } from "./lobbyEvents";
import { queryKeys } from "../queryKeys";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import { lobbyCacheReducer } from "./lobbyCacheReducer";

export function lobbyCacheDispatcher(
    queryClient: QueryClient,
    lobbyId: number,
    event: LobbyEvent
) {
    queryClient.setQueryData(queryKeys.lobby(lobbyId), (old: LobbyDto) => {
        return lobbyCacheReducer(old, event);
    });
}