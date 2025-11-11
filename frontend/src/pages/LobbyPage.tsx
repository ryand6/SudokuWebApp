import { LobbyChatPanel } from "@/components/lobby/LobbyChatPanel";
import { LobbyPlayersPanel } from "@/components/lobby/LobbyPlayersPanel";
import { LobbySettingsPanel } from "@/components/lobby/LobbySettingsPanel";
import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useGetLobby } from "@/hooks/lobby/useGetLobby";
import { useHandleGetLobbyError } from "@/hooks/lobby/useHandleGetLobbyError";
import { useValidateLobbyId } from "@/hooks/lobby/useValidateLobbyId";
import { useValidateLobbyUser } from "@/hooks/lobby/useValidateLobbyUser";
import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import { handleLobbyWebSocketMessages } from "@/services/websocket/handleLobbyWebSocketMessages";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import { toast } from "react-toastify";

export function LobbyPage() {
    const { lobbyId } = useParams();

    const id = lobbyId ? Number(lobbyId) : NaN;

    useValidateLobbyId(id);

    const queryClient = useQueryClient();
    const { subscribe, unsubscribe } = useWebSocketContext();
    
    const {data: lobby, isLoading: isLobbyLoading, isError: isLobbyError, error: lobbyError} = useGetLobby(id);
    const {data: currentUser, isLoading: isCurrentUserLoading } = useGetCurrentUser();
    const hasSubscribedRef = useRef(false);

    useHandleGetLobbyError(isLobbyError, lobbyError);

    useValidateLobbyUser(lobby, currentUser);

    useEffect(() => {
        if (!lobbyId || hasSubscribedRef.current) return;
        hasSubscribedRef.current = true;
        const lobbyIdNum = parseInt(lobbyId);
        const topic = `/topic/lobby/${lobbyId}`;
        const subscription = subscribe(topic, (body: any) => handleLobbyWebSocketMessages(body, queryClient, lobbyIdNum));

        console.log(subscription);

        return () => {
            if (subscription) unsubscribe(topic);
            hasSubscribedRef.current = false;
        };
    }, [lobbyId]);
    
    if (isLobbyLoading || isCurrentUserLoading) return <SpinnerButton />;

    if (!lobby || !currentUser) {
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
                <LobbySettingsPanel lobby={lobby} currentUser={currentUser} />
                <LobbyChatPanel lobby={lobby} currentUser={currentUser} />
            </div>
        </div>
    )
}