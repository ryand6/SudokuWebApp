import { confirmPlayerGameLoaded } from "@/api/ws/game/confirmPlayerGameLoaded";
import type { UserDto } from "@/types/dto/entity/user/UserDto";
import type { PrivateGamePlayerState, PublicGameState } from "@/types/game/GameTypes";
import { useEffect } from "react";

export function useConfirmPlayerGameLoaded(
    publicGameState: PublicGameState | undefined,
    privateGameState: PrivateGamePlayerState | undefined,
    currentUser: UserDto | undefined,
    send: (destination: string, body: any) => void
) {
     useEffect(() => {
        if ((publicGameState && privateGameState && currentUser) && !publicGameState.players[currentUser.id].gameLoaded) {
            confirmPlayerGameLoaded(send, publicGameState.gameId);
        }
    },[publicGameState, privateGameState, currentUser]);
}