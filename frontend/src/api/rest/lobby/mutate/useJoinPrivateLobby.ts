import { joinPrivateLobby } from "@/api/rest/lobby/mutate/joinPrivateLobby";
import { queryKeys } from "@/state/queryKeys";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom";

export function useJoinPrivateLobby() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation<LobbyDto, Error, string>({
        mutationFn: (token: string) => joinPrivateLobby(token),
        onSuccess: (data) => {
            queryClient.setQueryData(queryKeys.lobby(data.id), data);
            // redirect to the lobby once joined
            navigate(`/lobby/${data.id}`);
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}