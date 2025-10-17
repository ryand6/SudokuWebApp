import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { isCurrentUserInLobby } from "@/utils/lobby/isCurrentUserInLobby";
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

        const isUserInLobby = isCurrentUserInLobby(lobby, currentUser);
        
        if (!isUserInLobby) {
            toast.error("You are not an active player in this lobby");
            navigate("/dashboard", { replace: true });
        }
    }, [lobby, currentUser, navigate]);
}