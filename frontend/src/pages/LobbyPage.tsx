import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useGetLobby } from "@/hooks/lobby/useGetLobby";
import { useHandleGetLobbyError } from "@/hooks/lobby/useHandleGetLobbyError";
import { useValidateLobbyId } from "@/hooks/lobby/useValidateLobbyId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import { useParams } from "react-router-dom";


export function LobbyPage() {
    const { lobbyId } = useParams();

    const id = lobbyId ? Number(lobbyId) : NaN;

    useValidateLobbyId(id);
    
    const {data: lobby, isLoading: isLobbyLoading, isError: isLobbyError, error: lobbyError} = useGetLobby(id);
    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    useHandleGetLobbyError(isLobbyError, lobbyError);

    useValidateLobbyUser(lobby, currentUser);

    if (isLobbyLoading || isCurrentUserLoading) return <SpinnerButton />;

    return (
        <></>
    )
}