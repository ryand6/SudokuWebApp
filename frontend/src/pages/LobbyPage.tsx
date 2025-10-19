import { LobbyPlayersPanel } from "@/components/lobby/LobbyPlayersPanel";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useGetLobby } from "@/hooks/lobby/useGetLobby";
import { useHandleGetLobbyError } from "@/hooks/lobby/useHandleGetLobbyError";
import { useValidateLobbyId } from "@/hooks/lobby/useValidateLobbyId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";


export function LobbyPage() {
    const { lobbyId } = useParams();

    const id = lobbyId ? Number(lobbyId) : NaN;

    useValidateLobbyId(id);
    
    const {data: lobby, isLoading: isLobbyLoading, isError: isLobbyError, error: lobbyError} = useGetLobby(id);
    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();

    useHandleGetLobbyError(isLobbyError, lobbyError);

    useValidateLobbyUser(lobby, currentUser);

    if (isLobbyLoading || isCurrentUserLoading) return <SpinnerButton />;

    if (!lobby) {
        toast.error("An error has occurred.");
        return null;
    }

    return (
        <div id="lobby-container">
            <div id="lobby-header">
                <h1>{lobby?.lobbyName}</h1>
            </div>
            <div id="lobby-content">
                <LobbyPlayersPanel lobby={lobby} />
            </div>
        </div>
    )
}