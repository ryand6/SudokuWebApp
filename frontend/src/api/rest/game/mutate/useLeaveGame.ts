import { useWebSocketContext } from "@/context/WebSocketProvider";
import type { GameDto } from "@/types/dto/entity/game/GameDto";
import type { LeaveGameRequestDto } from "@/types/dto/request/LeaveGameRequestDto";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { leaveGame } from "./leaveGame";
import { queryKeys } from "@/state/queryKeys";

export function useLeaveGame() {
    const queryClient = useQueryClient();
    const navigate = useNavigate();
    const { unsubscribe } = useWebSocketContext();
    const [isLeaving, setIsLeaving] = useState(false);

    const mutation = useMutation<GameDto | null, Error, LeaveGameRequestDto>({
        mutationFn: ({gameId}) => leaveGame(gameId),
        onMutate: () => {
            setIsLeaving(true);
        },
        onSuccess: (updatedGame, variables) => {
            unsubscribe(`/topic/game/${variables.gameId}`);
            // handles when a game is closed
            if (updatedGame === null) {
                // Remove game and game player state caches
                queryClient.removeQueries({ queryKey: queryKeys.game(variables.gameId), exact: true });
                queryClient.removeQueries({ queryKey: queryKeys.gamePlayerState(variables.gameId, variables.userId), exact: true});
                navigate("/dashboard", { replace: true });
                return;
            } else {
                queryClient.setQueryData(queryKeys.game(variables.gameId), updatedGame);
                navigate("/dashboard", { replace: true });
            }
        },
        onError: (err: any) => {
            console.log("Leaving game error: ", err?.message ?? err);
        }
    })

    return { mutate: mutation.mutate, isLeaving };
}