import { joinPublicLobby } from "@/api/lobby/joinPublicLobby";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export function useJoinPublicLobby() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation<LobbyDto, Error, number>({
        mutationFn: (lobbyId: number) => joinPublicLobby(lobbyId),
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