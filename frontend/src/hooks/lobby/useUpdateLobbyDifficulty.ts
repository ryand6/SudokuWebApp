import { updateLobbyDifficulty } from "@/api/rest/lobby/updateLobbyDifficulty";
import type { UpdateLobbyDifficultyDto } from "@/types/dto/request/UpdateLobbyDifficultyDto"
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-toastify";

export function useUpdateLobbyDifficulty() {
    const queryClient = useQueryClient();

    return useMutation<LobbyDto, Error, UpdateLobbyDifficultyDto>({
        mutationFn: ({lobbyId, difficulty}) => updateLobbyDifficulty(lobbyId, difficulty),
        onSuccess: (updatedLobby, variables) => {
            const lobbyId = variables.lobbyId;
            queryClient.setQueryData(["lobby", lobbyId], updatedLobby);
        },
        onError: (error) => {
            toast.error(error.message);
        }
    })
}