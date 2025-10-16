import { joinPublicLobby } from "@/api/lobby/joinPublicLobby";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { handleLobbyWebSocketMessages } from "@/services/websocket/handleLobbyWebSocketMessages";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

export function useJoinPublicLobby() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { subscribe } = useWebSocketContext();

    return useMutation<LobbyDto, Error, number>({
        mutationFn: (lobbyId: number) => joinPublicLobby(lobbyId),
        onSuccess: (data) => {
            queryClient.setQueryData(["lobby", data.id], data);
            // Subscribe the user to the websocket topic to receive updates regarding joined lobby
            const topic = `/topic/lobby/${data.id}`;
            subscribe(topic, (body: any) => {
                handleLobbyWebSocketMessages(body, queryClient, data.id);
            })
            // redirect to the lobby once joined
            navigate(`/lobby/${data.id}`);
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}