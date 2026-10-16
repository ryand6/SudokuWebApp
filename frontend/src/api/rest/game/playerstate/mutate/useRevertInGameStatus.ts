import type { RevertInGameStatusResponseDto } from "@/types/dto/response/RevertInGameStatusResponseDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { revertInGameStatus } from "./revertInGameStatus";
import { queryKeys } from "@/state/queryKeys";
import type { LobbyDto } from "@/types/dto/entity/lobby/LobbyDto";

export function useRevertInGameStatus() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation<RevertInGameStatusResponseDto, Error, number>({
        mutationFn: (lobbyId: number) => revertInGameStatus(lobbyId),
        onSuccess: (data) => {
            queryClient.setQueryData(
                queryKeys.lobby(data.lobbyId),
                (oldLobby: LobbyDto | undefined) => {
                    if (!oldLobby) return oldLobby;
                    return {
                        ...oldLobby,
                        lobbyPlayers: oldLobby.lobbyPlayers.map(player => 
                            player.user.id === data.userId
                                ? {...player, lobbyStatus: data.lobbyStatus}
                                : player
                        )
                    }
                }
            );
            document.getElementById("root")?.classList.remove("blur-sm");
            // redirect to the lobby once joined
            navigate(`/lobby/${data.lobbyId}`);
        },
        onError: (error) => {
            alert(error.message);
        }
    })
}