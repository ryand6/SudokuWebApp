import { joinPrivateLobby } from "@/api/rest/lobby/mutate/joinPrivateLobby";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom";

export function useJoinPrivateLobby() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation<LobbyDto, Error, string>({
        mutationFn: (token: string) => joinPrivateLobby(token),
        onSuccess: (data) => {
            queryClient.setQueryData(["lobby", data.id], data);
            // redirect to the lobby once joined
            navigate(`/lobby/${data.id}`);
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}