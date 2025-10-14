import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function useValidateLobbyUser(
    lobby?: LobbyDto,
    currentUser?: UserDto
) {
    const navigate = useNavigate();

    useEffect(() => {
        if (!lobby || !currentUser) return;

        const isCurrentUserInLobby = lobby.lobbyPlayers.some(
            (player) => player.user.id === currentUser.id
        );
        
        if (!isCurrentUserInLobby) {
            toast.error("You are not an active player in this lobby");
            navigate("/dashboard", { replace: true });
        }
    }, [lobby, currentUser, navigate]);
}