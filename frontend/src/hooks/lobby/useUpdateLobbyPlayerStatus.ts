import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type UpdateLobbyPlayerStatusDto } from "@/types/dto/request/UpdateLobbyPlayerStatusDto";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { updateLobbyPlayerStatus } from "@/api/rest/lobby/updateLobbyPlayerStatus";
import { toast } from "react-toastify";

export function useUpdateLobbyPlayerStatus() {
    const queryClient = useQueryClient();

    return useMutation<LobbyDto, Error, UpdateLobbyPlayerStatusDto>({
        mutationFn: ({lobbyId, userId, lobbyStatus}) => updateLobbyPlayerStatus(lobbyId, userId, lobbyStatus),
        onSuccess: (updatedLobby, variables) => {
            const lobbyId = variables.lobbyId;
            queryClient.setQueryData(["lobby", lobbyId], updatedLobby);
        },
        onError: (error) => {
            toast.error(error.message);
        }
    })
    
}