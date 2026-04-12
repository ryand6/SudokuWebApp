import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type UpdateLobbyPlayerStatusDto } from "@/types/dto/request/UpdateLobbyPlayerStatusDto";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import { toast } from "react-toastify";
import { queryKeys } from "@/state/queryKeys";
import { updateLobbyPlayerStatus } from "./updateLobbyPlayerStatus";

export function useUpdateLobbyPlayerStatus() {
    const queryClient = useQueryClient();

    return useMutation<LobbyDto, Error, UpdateLobbyPlayerStatusDto>({
        mutationFn: ({lobbyId, userId, lobbyStatus}) => updateLobbyPlayerStatus(lobbyId, userId, lobbyStatus),
        onSuccess: (updatedLobby, variables) => {
            const lobbyId = variables.lobbyId;
            queryClient.setQueryData(queryKeys.lobby(lobbyId), updatedLobby);
        },
        onError: (error) => {
            toast.error(error.message, {containerId: "foreground"});
        }
    })
    
}