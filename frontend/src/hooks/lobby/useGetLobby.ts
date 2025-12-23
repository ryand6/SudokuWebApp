import { getLobby } from "@/api/rest/lobby/getLobby";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useQuery } from "@tanstack/react-query";

export function useGetLobby(lobbyId: number) {
    return useQuery({
        queryKey: ["lobby", lobbyId],
        queryFn: () => getLobby(lobbyId),
        // Only run if lobbyId has a value
        enabled: !!lobbyId,
        retry: false,
        // cache is updated by websocket connection, however to prevent websockets being the source of truth (incase critical updates are missed), allow revalidation to occur after 5 seconds
        staleTime: 5000,
        refetchOnWindowFocus: true,
        refetchOnReconnect: true,
        refetchInterval: (data: LobbyDto) => {
            if (!data) return 10000; // lobby unknown / loading
            if (data.inGame) return false; // terminal state
            if (data.countdownActive) return 2000; // critical
            return 10000; // idle
        }
    })
}