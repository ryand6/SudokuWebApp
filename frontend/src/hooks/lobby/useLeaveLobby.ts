import { leaveLobby } from "@/api/rest/lobby/leaveLobby";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { LeaveLobbyRequestDto } from "@/types/dto/request/LeaveLobbyRequestDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export function useLeaveLobby() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { unsubscribe } = useWebSocketContext();
    const [isLeaving, setIsLeaving] = useState(false);

    const mutation = useMutation<LobbyDto | null, Error, LeaveLobbyRequestDto>({
        mutationFn: ({lobbyId}) => leaveLobby(lobbyId),
        onMutate: () => {
            setIsLeaving(true)
        },
        onSuccess: (updatedLobby, variables) => {
            unsubscribe(`/topic/lobby/${variables.lobbyId}`);
            // handles when a lobby is closed
            if (updatedLobby === null) {
                // Reset public lobbies list and removed lobby caches to account for removal of the lobby from the backend
                queryClient.removeQueries({ queryKey: ["lobby", variables.lobbyId], exact: true });
                queryClient.resetQueries({ queryKey: ["publicLobbiesList"], exact: true });
                navigate("/dashboard", { replace: true });
                return;
            } else {
                const lobbyId = variables.lobbyId;
                queryClient.setQueryData(["lobby", lobbyId], updatedLobby);
                navigate("/dashboard", { replace: true });
            }
        },
        onError: (err: any) => {
            // Handle any error for display in UI
            console.error("Leaving Lobby error: ", err?.message || err);
        }
    })

    return { mutate: mutation.mutate, isLeaving };
}