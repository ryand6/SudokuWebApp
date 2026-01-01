import { leaveLobby } from "@/api/rest/lobby/leaveLobby";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { LeaveLobbyRequestDto } from "@/types/dto/request/LeaveLobbyRequestDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export function useLeaveLobby() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation<LobbyDto, Error, LeaveLobbyRequestDto>({
        mutationFn: ({lobbyId}) => leaveLobby(lobbyId),
        onSuccess: (updatedLobby, variables) => {
            const lobbyId = variables.lobbyId;
            queryClient.setQueryData(["lobby", lobbyId], updatedLobby);
            queryClient.removeQueries({ queryKey: ["publicLobbiesList"] });
            navigate("/dashboard", { replace: true });
        },
        onError: (err: any) => {
            // Handle any error for display in UI
            console.error("Leaving Lobby error: ", err?.message || err);
            // Handle lobby does not exist
            if (err.status === 404) {
                navigate("/dashboard", { replace: true });
            }
        }
    })
}