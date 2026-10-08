import { leaveLobby } from "@/api/rest/lobby/mutate/leaveLobby";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { queryKeys } from "@/state/queryKeys";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";
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
            setIsLeaving(true);
        },
        onSuccess: async (updatedLobby, variables) => {
            unsubscribe(`/topic/lobby/${variables.lobbyId}`);

            queryClient.setQueryData(
                queryKeys.userActiveLobby,
                null
            );

            if (updatedLobby === null) {
                queryClient.removeQueries({
                    queryKey: queryKeys.lobby(variables.lobbyId),
                    exact: true,
                });

                queryClient.resetQueries({
                    queryKey: queryKeys.publicLobbies,
                    exact: true,
                });
            } else {
                queryClient.setQueryData(
                    queryKeys.lobby(variables.lobbyId),
                    updatedLobby
                );
            }

            navigate("/dashboard", { replace: true });
        },
        onError: (err: any) => {
            // Handle any error for display in UI
            console.error("Leaving Lobby error: ", err?.message ?? err);
        }
    })

    return { mutate: mutation.mutate, isLeaving };
}