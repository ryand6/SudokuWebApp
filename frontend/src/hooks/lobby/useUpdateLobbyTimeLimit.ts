import { updateLobbyTimeLimit } from "@/api/rest/lobby/updateLobbyLimitLimit";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UpdateLobbyTimeLimitDto } from "@/types/dto/request/UpdateLobbyTimeLimitDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-toastify";

export function useUpdateLobbyTimeLimit() {
    const queryClient = useQueryClient();

    return useMutation<LobbyDto, Error, UpdateLobbyTimeLimitDto>({
        mutationFn: ({lobbyId, userId, timeLimit}) => updateLobbyTimeLimit(lobbyId, userId, timeLimit),
        onSuccess: (updatedLobby, variables) => {
            const lobbyId = variables.lobbyId;
            queryClient.setQueryData(["lobby", lobbyId], updatedLobby);
        },
        onError: (error) => {
            toast.error(error.message);
        }
    })
}