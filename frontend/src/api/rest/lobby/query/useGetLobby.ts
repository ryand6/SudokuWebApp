import { getLobby } from "@/api/rest/lobby/query/getLobby";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useQuery } from "@tanstack/react-query";

export function useGetLobby(lobbyId: number) {
    return useQuery<LobbyDto>({
        queryKey: ["lobby", lobbyId],
        queryFn: () => getLobby(lobbyId),
        // Only run if lobbyId has a value
        enabled: !!lobbyId,
        retry: false,
        // cache is updated by websocket connection, however to prevent websockets being the source of truth (incase critical updates are missed), allow revalidation to occur after 5 seconds
        staleTime: 5000,
        refetchOnWindowFocus: true,
        refetchOnReconnect: true,
        refetchInterval: (query): number | false => {
            const data = query.state.data;
            if (!data) return 10000; // lobby unknown or loading
            if (data.inGame) return false; // terminal state
            if (data.countdownActive) return 2000; // poll every 2 seconds to ensure any potential changes to countdown are accounted for
            return 10000; // idle
        }
    })
}