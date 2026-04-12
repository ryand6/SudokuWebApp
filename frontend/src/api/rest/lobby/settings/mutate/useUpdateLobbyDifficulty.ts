import type { UpdateLobbyDifficultyDto } from "@/types/dto/request/UpdateLobbyDifficultyDto"
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { queryKeys } from "@/state/queryKeys";
import { updateLobbyDifficulty } from "./updateLobbyDifficulty";

export function useUpdateLobbyDifficulty() {
    const queryClient = useQueryClient();

    return useMutation<LobbyDto, Error, UpdateLobbyDifficultyDto>({
        mutationFn: ({lobbyId, userId, difficulty}) => updateLobbyDifficulty(lobbyId, userId, difficulty),
        onSuccess: (updatedLobby, variables) => {
            const lobbyId = variables.lobbyId;
            queryClient.setQueryData(queryKeys.lobby(lobbyId), updatedLobby);
        },
        onError: (error) => {
            toast.error(error.message, {containerId: "foreground"});
        }
    })
}