import type { GameDto } from "@/types/dto/entity/GameDto";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { isCurrentUserInGame } from "@/utils/error/isCurrentUserInGame";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function useValidateGamePlayer(
    game?: GameDto,
    currentUser?: UserDto,
    isLeaving?: boolean
) {
    const navigate = useNavigate();

    useEffect(() => {
        if (!game || !currentUser || isLeaving) return;

        const isUserInGame = isCurrentUserInGame(game, currentUser);
        
        if (!isUserInGame) {
            toast.error("You are not an active player in this game");
            navigate("/dashboard", { replace: true });
        }
    }, [game, currentUser, navigate, isLeaving]);
}