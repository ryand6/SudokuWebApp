import { leaveLobby } from "@/api/rest/lobby/leaveLobby";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { LeaveLobbyRequestDto } from "@/types/dto/request/LeaveLobbyRequestDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export function useLeaveLobby() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation<LobbyDto | null, Error, LeaveLobbyRequestDto>({
        mutationFn: ({lobbyId}) => leaveLobby(lobbyId),
        onSuccess: (updatedLobby, variables) => {
            // handles when a lobby is closed
            if (updatedLobby === null) {
                // Reset public lobbies list and removed lobby caches to account for removal of the lobby from the backend
                queryClient.removeQueries({ queryKey: ["lobby", variables.lobbyId], exact: true });
                queryClient.resetQueries({ queryKey: ["publicLobbiesList"], exact: true });
                navigate("/dashboard", { replace: true });
                return;
            }
            const lobbyId = variables.lobbyId;
            queryClient.setQueryData(["lobby", lobbyId], updatedLobby);
            navigate("/dashboard", { replace: true });
        },
        onError: (err: any) => {
            // Handle any error for display in UI
            console.error("Leaving Lobby error: ", err?.message || err);
        }
    })
}