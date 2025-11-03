import { getLobbyMessages } from "@/services/lobby/lobbyMessagesService";
import { useQuery } from "@tanstack/react-query";

export function useLobbyMessages(lobbyId: number) { 
    return useQuery<{user: string, message: string}[], Error>({ 
        queryKey: ["lobbyChat", lobbyId],
        queryFn: () => getLobbyMessages(lobbyId), 
        // Only run if lobbyId has a value 
        enabled: !!lobbyId, 
        retry: false, 
        // cache is updated by websocket connection, therefore refetching data not required 
        staleTime: Infinity 
    });
}