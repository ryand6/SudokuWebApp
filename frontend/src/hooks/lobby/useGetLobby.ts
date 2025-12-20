import { getLobby } from "@/api/rest/lobby/getLobby";
import { useQuery } from "@tanstack/react-query";

export function useGetLobby(lobbyId: number) {
    return useQuery({
        queryKey: ["lobby", lobbyId],
        queryFn: () => getLobby(lobbyId),
        // Only run if lobbyId has a value
        enabled: !!lobbyId,
        retry: false,
        // cache is updated by websocket connection, therefore refetching data not required
        staleTime: Infinity
    })
}